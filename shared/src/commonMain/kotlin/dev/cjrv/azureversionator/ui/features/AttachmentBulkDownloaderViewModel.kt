package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class AttachmentBulkDownloaderViewModel : ViewModel() {


    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000L.milliseconds)
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun onWorkitemIdChange(value: String) =
        _state.update { it.copy(workitemId = value) }

    fun downloadAttachments() {


    }

    data class UIState(
        val isLoading: Boolean = true,
        val isDownloading: Boolean = false,
        val workitemId: String = "",
        val workitemIdError: String? = null,
    ) {
    }
}