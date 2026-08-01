package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.model.azure.AzureVariable
import dev.cjrv.azureversionator.data.settings.AzureSettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class EditProfileViewModel(private val settingsRepository : AzureSettingsRepository) : ViewModel() {
    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000L.milliseconds)
            loadVariables()
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadVariables() {
        val activeProfile = settingsRepository.getActiveProfile()
        _state.update {
            it.copy(
                selectedProfileId = activeProfile.id,
                selectedProfileName = activeProfile.name,
                teamProjectName = activeProfile.teamProjectName,
                variables = activeProfile.variables
            )
        }
    }

    fun addNewVariable(){
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

    fun onTeamProjectNameChanged(newValue: String) {
        _state.update { it.copy(teamProjectName = newValue) }
    }

    fun onSaveChangesClicked() = Unit

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

    data class UIState(
        val isLoading: Boolean = true,
        val selectedProfileId: String? = null,
        val selectedProfileName: String? = null,
        val teamProjectName : String = "",
        val variables : List<AzureVariable> = emptyList()
    )
}