package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.model.AzureDevOpsConfig
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
            val filters = settingsRepository.loadFilters()
            _state.update {
                it.copy(
                    isLoading = false,
                    organization = config.organization,
                    projectName = config.projectName,
                    personalAccessToken = config.personalAccessToken,
                    branchFilter = filters.branchFilter.joinToString(separator = ";"),
                    pipelineFilter = filters.pipelineFilter.joinToString(separator = ";"),
                    repositoryFilter = filters.repositoryFilter.joinToString(separator = ";")
                )
            }
        }
    }

    fun onOrganizationChange(value: String) =
        _state.update { it.copy(organization = value) }

    fun onProjectNameChange(value: String) =
        _state.update { it.copy(projectName = value) }

    fun onPersonalAccessTokenChange(value: String) =
        _state.update { it.copy(personalAccessToken = value) }

    fun onBranchFilterChange(value: String) =
        _state.update { it.copy(branchFilter = value) }

    fun onRepositoryFilterChange(value: String) =
        _state.update { it.copy(repositoryFilter = value) }

    fun onPipelineFilterChange(value: String) =
        _state.update { it.copy(pipelineFilter = value) }

    fun saveSettings() {
        if (!validate()) return
        val s = _state.value
        settingsRepository.saveConfig(
            AzureDevOpsConfig(
                organization = s.organization,
                projectName = s.projectName,
                personalAccessToken = s.personalAccessToken
            )
        ).also {
            settingsRepository.saveFilters(
                branches = s.branchFilter,
                pipelines = s.pipelineFilter,
                repositories = s.repositoryFilter
            )
        }
        _state.update { it.copy(savedFeedback = true) }
    }

    fun onSavedFeedbackConsumed() =
        _state.update { it.copy(savedFeedback = false) }

    private fun validate(): Boolean {
        val s = _state.value
        _state.update {
            it.copy(
                organizationError = if (s.organization.isBlank()) "Required" else null,
                projectNameError = if (s.projectName.isBlank()) "Required" else null,
                personalAccessTokenError = if (s.personalAccessToken.isBlank()) "Required" else null
            )
        }
        val updated = _state.value
        return listOf(
            updated.organizationError,
            updated.projectNameError,
            updated.personalAccessTokenError
        ).all { it == null }
    }

    data class UIState(
        val isLoading: Boolean = true,
        val organization: String = "",
        val projectName: String = "",
        val personalAccessToken: String = "",
        val branchFilter: String = "",
        val pipelineFilter: String = "",
        val repositoryFilter: String = "",
        val organizationError: String? = null,
        val projectNameError: String? = null,
        val personalAccessTokenError: String? = null,
        val branchFilterError: String? = null,
        val pipelineFilterError: String? = null,
        val repositoryFilterError: String? = null,
        val savedFeedback: Boolean = false,
    )
}