package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.openurl.OpenUrlService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(private val openUrlService: OpenUrlService) : ViewModel() {

    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000L.milliseconds)
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun openAboutMe() {
        openUrlService.openInBrowser("https://github.com/cjrvdev")
    }

    data class UIState(
        val isLoading: Boolean = true,
        val selectedProfileId : String? = null,
        val profiles : List<String> = listOf("Profile 1", "Profile 2", "Profile 3")
    )
}