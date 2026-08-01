package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsConfig
import dev.cjrv.azureversionator.data.settings.AzureSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: AzureSettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val config = settingsRepository.loadConfig()
            _state.update {
                it.copy(
                    isLoading = false,
                    organization = config.organization,
                    personalAccessToken = config.personalAccessToken
                )
            }
        }
    }

    fun onOrganizationChange(value: String) =
        _state.update { it.copy(organization = value) }

    fun onPersonalAccessTokenChange(value: String) =
        _state.update { it.copy(personalAccessToken = value) }

    fun saveSettings() {
        if (!validate()) return
        val s = _state.value
        settingsRepository.saveConfig(
            AzureDevOpsConfig(
                organization = s.organization,
                personalAccessToken = s.personalAccessToken
            )
        )
        _state.update { it.copy(savedFeedback = true) }
    }

    fun onSavedFeedbackConsumed() =
        _state.update { it.copy(savedFeedback = false) }

    private fun validate(): Boolean {
        val s = _state.value
        _state.update {
            it.copy(
                organizationError = if (s.organization.isBlank()) "Required" else null,
                personalAccessTokenError = if (s.personalAccessToken.isBlank()) "Required" else null
            )
        }
        val updated = _state.value
        return listOf(
            updated.organizationError,
            updated.personalAccessTokenError
        ).all { it == null }
    }

    data class UIState(
        val isLoading: Boolean = true,
        val organization: String = "",
        val personalAccessToken: String = "",
        val organizationError: String? = null,
        val personalAccessTokenError: String? = null,
        val savedFeedback: Boolean = false,
    )
}