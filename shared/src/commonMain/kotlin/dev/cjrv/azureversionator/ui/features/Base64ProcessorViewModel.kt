package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64

class Base64ProcessorViewModel : ViewModel() {
    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    fun onModeSelected(mode: ProcessorMode) {
        _state.update { it.copy(mode = mode, errorMessage = null, successMessage = null) }
    }

    fun onTextInputChanged(value: String) {
        _state.update {
            it.copy(
                textInput = value,
                isTextInputBase64 = value.isValidBase64(),
                errorMessage = null
            )
        }
    }

    fun onImageInputChanged(value: String) {
        _state.update { it.copy(imageInput = value, errorMessage = null) }
    }

    fun encodeText() = transformText { Base64.encode(it.encodeToByteArray()) }

    fun decodeText() = transformText { Base64.decode(it).decodeToString() }

    fun selectImage() {
        viewModelScope.launch {
            try {
                val imageFile = FileKit.openFilePicker(FileKitType.Image) ?: return@launch
                val bytes = imageFile.readBytes()
                require(bytes.isNotEmpty()) { "The selected image is empty." }

                _state.update {
                    it.copy(
                        imageResult = "data:${bytes.imageMimeType()};base64,${Base64.encode(bytes)}",
                        errorMessage = null
                    )
                }
            } catch (exception: Exception) {
                _state.update { it.copy(errorMessage = "Unable to read the selected image: ${exception.message}") }
            }
        }
    }

    fun saveImage() {
        val value = _state.value.imageInput.ifBlank { _state.value.imageResult }.trim()
        if (value.isBlank()) {
            _state.update { it.copy(errorMessage = "Enter a Base64 image before saving.") }
            return
        }

        viewModelScope.launch {
            try {
                val image = value.toImageData()
                val file = FileKit.openFileSaver(
                    suggestedName = "image",
                    defaultExtension = image.extension,
                    allowedExtensions = setOf(image.extension)
                ) ?: return@launch

                file.write(image.bytes)
                _state.update {
                    it.copy(
                        errorMessage = null,
                        successMessage = "Image saved successfully."
                    )
                }
            } catch (exception: Exception) {
                _state.update { it.copy(errorMessage = "Unable to save the Base64 image: ${exception.message}") }
            }
        }
    }

    fun onMessageConsumed() {
        _state.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun transformText(transform: (String) -> String) {
        val value = _state.value.textInput
        if (value.isBlank()) return

        _state.update {
            it.copy(
                textResult = transform(value),
                errorMessage = null
            )
        }
    }

    private fun String.toImageData(): ImageData {
        val dataUriPrefix = "data:"
        val base64Separator = ";base64,"
        val hasDataUri = startsWith(dataUriPrefix, ignoreCase = true)
        val separatorIndex = indexOf(base64Separator, ignoreCase = true)
        require(!hasDataUri || separatorIndex > dataUriPrefix.length) {
            "The data URI must include an image MIME type and Base64 data."
        }

        val mimeType = if (hasDataUri) {
            substring(dataUriPrefix.length, separatorIndex).lowercase()
        } else {
            "image/png"
        }
        require(mimeType in imageMimeTypes) { "Unsupported image type: $mimeType" }

        val encodedData = if (hasDataUri) substring(separatorIndex + base64Separator.length) else this
        return ImageData(
            bytes = Base64.decode(encodedData),
            extension = imageExtensions.getValue(mimeType)
        )
    }

    private data class ImageData(val bytes: ByteArray, val extension: String)

    data class UIState(
        val mode: ProcessorMode = ProcessorMode.Text,
        val textInput: String = "",
        val isTextInputBase64: Boolean = false,
        val textResult: String = "",
        val imageInput: String = "",
        val imageResult: String = "",
        val errorMessage: String? = null,
        val successMessage: String? = null
    )

    enum class ProcessorMode { Text, Image }

    private companion object {
        val imageExtensions = mapOf(
            "image/png" to "png",
            "image/jpeg" to "jpg",
            "image/gif" to "gif",
            "image/webp" to "webp",
            "image/bmp" to "bmp"
        )
        val imageMimeTypes = imageExtensions.keys

        fun ByteArray.imageMimeType(): String = when {
            size >= 8 && copyOfRange(0, 8).contentEquals(
                byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
            ) -> "image/png"
            size >= 3 && this[0] == 0xFF.toByte() && this[1] == 0xD8.toByte() && this[2] == 0xFF.toByte() ->
                "image/jpeg"
            size >= 6 && decodeToString(0, 6) in setOf("GIF87a", "GIF89a") -> "image/gif"
            size >= 12 && decodeToString(0, 4) == "RIFF" && decodeToString(8, 12) == "WEBP" -> "image/webp"
            size >= 2 && this[0] == 'B'.code.toByte() && this[1] == 'M'.code.toByte() -> "image/bmp"
            else -> throw IllegalArgumentException("The selected file is not a supported image.")
        }

        fun String.isValidBase64(): Boolean =
            isNotBlank() && runCatching { Base64.decode(this) }.isSuccess
    }
}