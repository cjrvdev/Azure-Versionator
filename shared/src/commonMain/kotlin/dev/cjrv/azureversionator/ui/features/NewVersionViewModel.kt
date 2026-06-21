package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NewVersionViewModel  : ViewModel(){
    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {

        }
    }

    fun onReleaseNotesChange(value: String) {
        _state.value = _state.value.copy(releaseNotes = value)
    }

    fun onVersionNameChange(value: String) {
        _state.value = _state.value.copy(versionName = value)
    }

    fun onBuildNumberChange(value: String) {
        _state.value = _state.value.copy(buildNumber = value)
    }

    fun createVersion(){
        if (!validate())
            return;
    }

    fun validate() : Boolean{
        var isValid = true
        val s = _state.value

        if (s.versionName.isBlank()){
            _state.value = _state.value.copy(versionNameError = "Version name cannot be empty")
            isValid = false
        } else {
            _state.value = _state.value.copy(versionNameError = null)
        }

        if (s.buildNumber.isBlank()){
            _state.value = _state.value.copy(buildNumberError = "Build number cannot be empty")
            isValid = false
        } else {
            _state.value = _state.value.copy(buildNumberError = null)
        }

        if (s.releaseNotes.isBlank()){
            _state.value = _state.value.copy(releaseNotesError = "Release notes cannot be empty")
            isValid = false
        } else {
            _state.value = _state.value.copy(releaseNotesError = null)
        }

        return isValid
    }

    data class UIState(
        val isLoading: Boolean = false,
        val versionName : String = "",
        val versionNameError : String? = null,
        val buildNumber : String = "",
        val buildNumberError : String? = null,
        val releaseNotes : String = "",
        val releaseNotesError : String? = null
    )
}