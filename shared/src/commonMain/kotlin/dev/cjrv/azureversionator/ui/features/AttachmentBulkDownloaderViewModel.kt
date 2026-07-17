package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.files.AttachmentFilePayload
import dev.cjrv.azureversionator.data.files.AttachmentFileService
import dev.cjrv.azureversionator.data.network.AzureDevOpsApi
import dev.cjrv.azureversionator.data.settings.AzureSettingsRepository
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AttachmentBulkDownloaderViewModel(
    private val settingsRepository: AzureSettingsRepository,
    private val azureDevOpsApi: AzureDevOpsApi,
    private val attachmentFileService: AttachmentFileService
) : ViewModel() {

    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val config = settingsRepository.loadConfig()
            val isConfigValid = config.organization.isNotBlank() &&
                    config.projectName.isNotBlank() &&
                    config.personalAccessToken.isNotBlank()

            _state.update {
                it.copy(
                    isLoading = false,
                    isConfigurationValid = isConfigValid,
                    destinationPath = attachmentFileService.defaultDownloadDirectoryPath(),
                    generalError = if (isConfigValid) null
                    else "Azure DevOps configuration is incomplete. Please configure settings first."
                )
            }
        }
    }

    fun onWorkitemIdChange(value: String) =
        _state.update { it.copy(workitemId = value, workitemIdError = null) }

    fun downloadAttachments() {
        if (!_state.value.isConfigurationValid) return
        if (!validate()) return

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    generalError = null,
                    successMessage = null
                )
            }

            val config = settingsRepository.loadConfig()
            val workItemId = _state.value.workitemId.trim()

            val attachments = azureDevOpsApi.getWorkItemAttachments(config, workItemId)
                .getOrElse { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = "Failed to load attachments: ${error.message}"
                        )
                    }
                    return@launch
                }

            if (attachments.isEmpty()) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        generalError = "No active attachments found in work item $workItemId."
                    )
                }
                return@launch
            }

            val filesToSave = mutableListOf<AttachmentFilePayload>()
            var fileNumber = 0

            for (attachment in attachments) {
                fileNumber++
                _state.update {
                    it.copy(
                        downloadingMessageCurrentFileIndex = fileNumber.toFloat(),
                        downloadingMessageFileTotalAmount = attachments.size.toFloat(),
                        downloadingMessage = "Downloading attachment $fileNumber of ${attachments.size}"
                    )
                }

                val fileBytes = azureDevOpsApi.downloadAttachment(
                    config = config,
                    attachmentUrl = attachment.url,
                    fileName = attachment.fileName
                ).getOrElse { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = "Failed to download '${attachment.fileName}': ${error.message}"
                        )
                    }
                    return@launch
                }

                filesToSave.add(
                    AttachmentFilePayload(
                        fileName = attachment.fileName,
                        content = fileBytes
                    )
                )
            }

            val destinationPath = _state.value.destinationPath.trim()
            attachmentFileService.saveFiles(destinationPath, filesToSave)
                .onSuccess { savedCount ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Downloaded $savedCount attachment(s) to '$destinationPath'.",
                            downloadingMessageFileTotalAmount = null,
                            downloadingMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = "Failed to save files: ${error.message}",
                            downloadingMessageFileTotalAmount = null,
                            downloadingMessage = null
                        )
                    }
                }
        }
    }

    fun onSuccessMessageConsumed() =
        _state.update { it.copy(successMessage = null) }

    fun onErrorConsumed() =
        _state.update { it.copy(generalError = null) }

    private fun validate(): Boolean {
        val currentState = _state.value
        val workItemId = currentState.workitemId.trim()
        val destination = currentState.destinationPath.trim()

        val workItemError = when {
            workItemId.isBlank() -> "Required"
            workItemId.any { !it.isDigit() } -> "Work item ID must be numeric"
            else -> null
        }

        val destinationError = when {
            destination.isBlank() -> "Required"
            else -> null
        }

        _state.update {
            it.copy(
                workitemIdError = workItemError,
                destinationPathError = destinationError
            )
        }

        return workItemError == null && destinationError == null
    }

    fun onSelectDownloadPath() {
        viewModelScope.launch {
            val directory = FileKit.openDirectoryPicker()
            _state.update {
                it.copy(
                    destinationPath = directory?.absolutePath() ?: it.destinationPath,
                    destinationPathError = null
                )
            }
        }
    }
}

data class UIState(
    val isLoading: Boolean = true,
    val isConfigurationValid: Boolean = true,
    val workitemId: String = "",
    val workitemIdError: String? = null,
    val destinationPath: String = "",
    val destinationPathError: String? = null,
    val downloadingMessage: String? = null,
    val downloadingMessageCurrentFileIndex: Float? = null,
    val downloadingMessageFileTotalAmount: Float? = null,
    val successMessage: String? = null,
    val generalError: String? = null
)
