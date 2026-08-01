package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.model.app.Profile
import dev.cjrv.azureversionator.data.openurl.OpenUrlService
import dev.cjrv.azureversionator.data.settings.AzureSettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(private val openUrlService: OpenUrlService, private val settingsRepository: AzureSettingsRepository) : ViewModel() {

    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000L.milliseconds)
            loadProfiles()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadProfiles() {
        val profiles = settingsRepository.loadProfiles()
        _state.update { it.copy(profiles = profiles, selectedProfileId = profiles.first().id, selectedProfileName = profiles.first().name) }
    }

    fun onSelectedProfileChanged(profileId: String) {
        val selectedProfile = _state.value.profiles.find { it.id == profileId }
        _state.update { it.copy(selectedProfileId = profileId, selectedProfileName = selectedProfile?.name) }
    }

    fun openAboutMe() {
        openUrlService.openInBrowser("https://github.com/cjrvdev")
    }

    fun createNewProfile() {
        val createdProfile = settingsRepository.createNewProfile()
        loadProfiles()
        onSelectedProfileChanged(createdProfile.id)
    }

    data class UIState(
        val isLoading: Boolean = true,
        val selectedProfileId : String? = null,
        val selectedProfileName : String? = null,
        val profiles : List<Profile> = emptyList()
    )
}