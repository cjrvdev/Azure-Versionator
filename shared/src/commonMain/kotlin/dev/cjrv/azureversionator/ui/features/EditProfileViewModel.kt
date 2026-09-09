package dev.cjrv.azureversionator.ui.features

import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsPreferencesFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.model.azure.AzureVariable
import dev.cjrv.azureversionator.data.model.azure.TextFieldType
import dev.cjrv.azureversionator.data.settings.AzureSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(private val settingsRepository: AzureSettingsRepository) : ViewModel() {
    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            loadProfileConfiguration()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadProfileConfiguration() {
        val activeProfile = settingsRepository.getActiveProfile()
        _state.update {
            it.copy(
                selectedProfileId = activeProfile.id,
                selectedProfileName = activeProfile.name,
                profileName = activeProfile.name,
                teamProjectName = activeProfile.teamProjectName,
                organizationName = activeProfile.organizationName,
                personalAccessToken = activeProfile.personalAccessToken,
                variables = activeProfile.variables,
                branchFilter = activeProfile.filters.branchFilter.joinToString(";"),
                pipelineFilter = activeProfile.filters.pipelineFilter.joinToString(";"),
                repositoryFilter = activeProfile.filters.repositoryFilter.joinToString(";")
            )
        }
    }

    fun addNewVariable() {
        val newVariable = AzureVariable(name = "", value = "", isSecret = false)
        _state.update { current ->
            current.copy(variables = current.variables + newVariable)
        }
    }

    fun removeVariable(index: Int) {
        _state.update { current ->
            if (index !in current.variables.indices) {
                current
            } else {
                current.copy(variables = current.variables.filterIndexed { variableIndex, _ -> variableIndex != index })
            }
        }
    }

    fun onVariableNameChanged(index: Int, newValue: String) {
        updateVariable(index) { variable ->
            variable.copy(name = newValue)
        }
    }

    fun onVariableValueChanged(index: Int, newValue: String) {
        updateVariable(index) { variable ->
            variable.copy(value = newValue)
        }
    }

    fun onVariableSecretChanged(index: Int, isSecret: Boolean) {
        updateVariable(index) { variable ->
            variable.copy(isSecret = isSecret)
        }
    }

    fun onVariableTextFieldTypeChanged(index: Int, textFieldType: TextFieldType) {
        updateVariable(index) { variable ->
            variable.copy(textFieldType = textFieldType)
        }
    }

    fun onVariableRequiredChanged(index: Int, isRequired: Boolean) {
        updateVariable(index) { variable ->
            variable.copy(isRequired = isRequired)
        }
    }

    fun onProfileNameChanged(newValue: String) {
        _state.update { it.copy(profileName = newValue) }
    }

    fun onTeamProjectNameChanged(newValue: String) {
        _state.update { current ->
            current.copy(
                teamProjectName = newValue,
                teamProjectNameError = current.showValidationErrors && newValue.isBlank()
            )
        }
    }

    fun onOrganizationChange(newValue: String) =
        _state.update {
            it.copy(
                organizationName = newValue,
                organizationNameError = it.showValidationErrors && newValue.isBlank()
            )
        }

    fun onPersonalAccessTokenChange(newValue: String) =
        _state.update {
            it.copy(
                personalAccessToken = newValue,
                personalAccessTokenError = it.showValidationErrors && newValue.isBlank()
            )
        }

    fun onBranchFilterChanged(newValue: String) {
        _state.update { it.copy(branchFilter = newValue) }
    }

    fun onPipelineFilterChanged(newValue: String) {
        _state.update { it.copy(pipelineFilter = newValue) }
    }

    fun onRepositoryFilterChanged(newValue: String) {
        _state.update { it.copy(repositoryFilter = newValue) }
    }

    fun removeProfile() {
        settingsRepository.deleteProfile(settingsRepository.getActiveProfile())
    }

    fun onSaveChangesClicked() {
        if (!validate()) {
            _state.update { it.copy(saveFeedback = SaveFeedback.ValidationError) }
            return
        }
        val activeProfile = settingsRepository.getActiveProfile()
        settingsRepository.saveProfile(
            activeProfile.copy(
                name = state.value.profileName,
                teamProjectName = state.value.teamProjectName,
                organizationName = state.value.organizationName,
                personalAccessToken = state.value.personalAccessToken,
                variables = state.value.variables,
                filters = AzureDevOpsPreferencesFilter(
                    branchFilter = state.value.branchFilter.splitFilterValues(),
                    pipelineFilter = state.value.pipelineFilter.splitFilterValues(),
                    repositoryFilter = state.value.repositoryFilter.splitFilterValues()
                )
            )
        )
        _state.update { it.copy(saveFeedback = SaveFeedback.Saved, showValidationErrors = false) }
    }

    fun onSaveFeedbackConsumed() {
        _state.update { it.copy(saveFeedback = null) }
    }

    private fun updateVariable(index: Int, transform: (AzureVariable) -> AzureVariable) {
        _state.update { current ->
            if (index !in current.variables.indices) {
                current
            } else {
                current.copy(
                    variables = current.variables.mapIndexed { variableIndex, variable ->
                        if (variableIndex == index) {
                            transform(variable)
                        } else {
                            variable
                        }
                    }
                )
            }
        }
    }

    private fun validate(): Boolean {
        val currentState = _state.value
        val teamProjectNameError = currentState.teamProjectName.isBlank()
        val organizationNameError = currentState.organizationName.isBlank()
        val personalAccessTokenError = currentState.personalAccessToken.isBlank()
        val hasInvalidVariables = currentState.variables.any {
            it.name.isBlank()
        }
        _state.update {
            it.copy(
                teamProjectNameError = teamProjectNameError,
                organizationNameError = organizationNameError,
                personalAccessTokenError = personalAccessTokenError,
                showValidationErrors = true
            )
        }
        return !teamProjectNameError && !organizationNameError && !personalAccessTokenError && !hasInvalidVariables
    }

    private fun String.splitFilterValues(): List<String> =
        split(';').map { it.trim() }.filter { it.isNotEmpty() }

    enum class SaveFeedback {
        Saved,
        ValidationError
    }

    data class UIState(
        val isLoading: Boolean = true,
        val selectedProfileId: String? = null,
        val selectedProfileName: String? = null,
        val profileName: String = "",
        val organizationName: String = "",
        val personalAccessToken: String = "",
        val teamProjectName: String = "",
        val branchFilter: String = "",
        val pipelineFilter: String = "",
        val repositoryFilter: String = "",
        val organizationNameError: Boolean = false,
        val personalAccessTokenError: Boolean = false,
        val teamProjectNameError: Boolean = false,
        val variables: List<AzureVariable> = emptyList(),
        val showValidationErrors: Boolean = false,
        val saveFeedback: SaveFeedback? = null
    )
}