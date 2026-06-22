package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.model.AzureDevOpsConfig
import dev.cjrv.azureversionator.data.model.PipelineVariables
import dev.cjrv.azureversionator.data.network.AzureDevOpsApi
import dev.cjrv.azureversionator.data.network.AzureBranch
import dev.cjrv.azureversionator.data.network.AzureRepository
import dev.cjrv.azureversionator.data.settings.AzureSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewVersionViewModel(
    private val settingsRepository: AzureSettingsRepository,
    private val azureDevOpsApi: AzureDevOpsApi
) : ViewModel() {
    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Pre-load Azure configuration to validate it exists
            val config = settingsRepository.loadConfig()
            val isConfigValid = config.organization.isNotBlank() && config.projectName.isNotBlank() &&
                config.personalAccessToken.isNotBlank() && config.pipelineId.isNotBlank()

            if (!isConfigValid) {
                _state.value = _state.value.copy(
                    isConfigurationValid = false,
                    generalError = "Azure DevOps configuration is incomplete. Please configure settings first."
                )
            } else {
                _state.value = _state.value.copy(isConfigurationValid = true)
                loadRepositories(config)
            }
            loadRepositories()
        }
    }

    fun loadRepositories() {
        viewModelScope.launch {
            val config = settingsRepository.loadConfig()
            val isConfigValid = config.organization.isNotBlank() && config.projectName.isNotBlank() &&
                config.personalAccessToken.isNotBlank() && config.pipelineId.isNotBlank()

            if (!isConfigValid) {
                _state.value = _state.value.copy(
                    repositoriesError = "Azure DevOps configuration is incomplete.",
                    repositories = emptyList()
                )
                return@launch
            }

            loadRepositories(config)
        }
    }

    fun loadBranches(repositoryId: String) {
        if (repositoryId.isBlank()) {
            _state.value = _state.value.copy(
                selectedRepositoryId = null,
                branches = emptyList(),
                branchesError = null
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                selectedRepositoryId = repositoryId,
                isLoadingBranches = true,
                branchesError = null
            )

            val config = settingsRepository.loadConfig()
            azureDevOpsApi.getBranches(config, repositoryId)
                .onSuccess { branches ->
                    _state.value = _state.value.copy(
                        isLoadingBranches = false,
                        branches = branches,
                        branchesError = null
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoadingBranches = false,
                        branches = emptyList(),
                        branchesError = "Failed to load branches: ${error.message}"
                    )
                }
        }
    }

    fun onReleaseNotesChange(value: String) {
        _state.value = _state.value.copy(releaseNotes = value, releaseNotesError = null)
    }

    fun onVersionNameChange(value: String) {
        _state.value = _state.value.copy(versionName = value, versionNameError = null)
    }

    fun onBuildNumberChange(value: String) {
        _state.value = _state.value.copy(buildNumber = value, buildNumberError = null)
    }

    fun onRepositoryIdChange(value: String) =
        _state.update { it.copy(repositoryId = value) }

    fun onBranchNameChange(value: String) =
        _state.update { it.copy(selectedBranchId = value) }

    fun createVersion() {
        if (!_state.value.isConfigurationValid) return
        if (!validate()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)

            try {
                val config = settingsRepository.loadConfig()
                val variables = PipelineVariables(
                    versionName = _state.value.versionName,
                    versionCode = _state.value.buildNumber,
                    releaseNotes = _state.value.releaseNotes
                )

                val result = azureDevOpsApi.runPipeline(config, variables)

                result.onSuccess { response ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Pipeline triggered successfully (Run ID: ${response.id})",
                        versionName = "",
                        buildNumber = "",
                        releaseNotes = ""
                    )
                }.onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        generalError = "Failed to trigger pipeline: ${error.message}"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    generalError = "Error: ${e.message}"
                )
            }
        }
    }

    fun onSuccessMessageConsumed() {
        _state.value = _state.value.copy(successMessage = null)
    }

    fun onErrorConsumed() {
        _state.value = _state.value.copy(generalError = null)
    }

    private suspend fun loadRepositories(config: AzureDevOpsConfig) {
        _state.value = _state.value.copy(isLoadingRepositories = true, repositoriesError = null)

        azureDevOpsApi.getRepositories(config)
            .onSuccess { repositories ->
                _state.value = _state.value.copy(
                    isLoadingRepositories = false,
                    repositories = repositories,
                    repositoriesError = null
                )
            }
            .onFailure { error ->
                _state.value = _state.value.copy(
                    isLoadingRepositories = false,
                    repositories = emptyList(),
                    repositoriesError = "Failed to load repositories: ${error.message}"
                )
            }
    }

    fun validate(): Boolean {
        var isValid = true
        val s = _state.value

        if (s.versionName.isBlank()) {
            _state.value = _state.value.copy(versionNameError = "Version name cannot be empty")
            isValid = false
        } else {
            _state.value = _state.value.copy(versionNameError = null)
        }

        if (s.buildNumber.isBlank()) {
            _state.value = _state.value.copy(buildNumberError = "Build number cannot be empty")
            isValid = false
        } else {
            _state.value = _state.value.copy(buildNumberError = null)
        }

        if (s.releaseNotes.isBlank()) {
            _state.value = _state.value.copy(releaseNotesError = "Release notes cannot be empty")
            isValid = false
        } else {
            _state.value = _state.value.copy(releaseNotesError = null)
        }

        if (s.repositoryId.isBlank()) {
            _state.value = _state.value.copy(repositoryIdError = "Repository ID cannot be empty")
            isValid = false
        } else {
            _state.value = _state.value.copy(repositoryIdError = null)
        }

        return isValid
    }

    data class UIState(
        val isLoading: Boolean = false,
        val isConfigurationValid: Boolean = true,
        val isLoadingRepositories: Boolean = false,
        val isLoadingBranches: Boolean = false,
        val repositories: List<AzureRepository> = emptyList(),
        val branches: List<AzureBranch> = emptyList(),
        val selectedRepositoryId: String? = null,
        val selectedBranchId: String? = null,
        val repositoriesError: String? = null,
        val branchesError: String? = null,
        val versionName: String = "",
        val versionNameError: String? = null,
        val buildNumber: String = "",
        val buildNumberError: String? = null,
        val releaseNotes: String = "",
        val releaseNotesError: String? = null,
        val repositoryId: String = "",
        val repositoryIdError: String? = null,
        val branchName: String = "",
        val branchNameError: String? = null,
        val successMessage: String? = null,
        val generalError: String? = null
    )
}