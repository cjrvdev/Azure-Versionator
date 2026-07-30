package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.model.azure.AzureBranch
import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsConfig
import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsPreferencesFilter
import dev.cjrv.azureversionator.data.model.azure.AzurePipeline
import dev.cjrv.azureversionator.data.model.azure.AzureRepository
import dev.cjrv.azureversionator.data.model.azure.PipelineVariables
import dev.cjrv.azureversionator.data.network.AzureDevOpsApi
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
                config.personalAccessToken.isNotBlank()

            if (!isConfigValid) {
                _state.value = _state.value.copy(
                    isConfigurationValid = false,
                    generalError = "Azure DevOps configuration is incomplete. Please configure settings first."
                )
            } else {
                _state.value = _state.value.copy(isConfigurationValid = true, isLoading = true)
                var filters = settingsRepository.loadFilters()
                loadRepositories(config, filters)
                loadPipelines(config, filters)
                _state.value = _state.value.copy(isLoading = false)
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

    fun onRepositorySelected(repository: AzureRepository) {
        _state.update {
            it.copy(
                selectedRepositoryId = repository.id,
                repositoryIdError = null,
                selectedBranchId = null,
                branchNameError = null,
                branches = emptyList(),
                loadBranchesError = null
            )
        }
        loadBranches(repository.id)
    }

    fun onBranchSelected(branch: AzureBranch) {
        _state.update {
            it.copy(
                selectedBranchId = branch.fullName,
                branchNameError = null
            )
        }
    }

    fun onPipelineSelected(pipeline: AzurePipeline) {
        _state.update {
            it.copy(
                selectedPipelineId = pipeline.id,
                pipelineIdError = null,
                loadPipelinesError = null
            )
        }
    }

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

                val result = azureDevOpsApi.runPipeline(
                    config = config,
                    variables = variables,
                    pipelineId = _state.value.selectedPipelineId.orEmpty(),
                    branchName = _state.value.selectedBranchId
                )

                result.onSuccess { response ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        successMessage = "Pipeline triggered successfully (Run ID: ${response.id})"
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

    fun loadBranches(repositoryId: String) {
        if (repositoryId.isBlank()) {
            _state.value = _state.value.copy(
                selectedRepositoryId = null,
                selectedBranchId = null,
                branches = emptyList(),
                loadBranchesError = null
            )
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                selectedRepositoryId = repositoryId,
                isLoadingBranches = true,
                loadBranchesError = null,
                isLoading = true
            )

            val config = settingsRepository.loadConfig()
            val filters = settingsRepository.loadFilters()
            azureDevOpsApi.getBranches(config, repositoryId)
                .onSuccess { branches ->
                    _state.value = _state.value.copy(
                        isLoadingBranches = false,
                        branches = sortWithFilteredFirst(branches, filters.branchFilter) { it.name },
                        loadBranchesError = null,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoadingBranches = false,
                        branches = emptyList(),
                        loadBranchesError = "Failed to load branches: ${error.message}",
                        isLoading = false
                    )
                }
        }
    }

    private suspend fun loadRepositories(
        config: AzureDevOpsConfig,
        filters: AzureDevOpsPreferencesFilter
    ) {
        _state.value = _state.value.copy(isLoadingRepositories = true, loadRepositoriesError = null)

        azureDevOpsApi.getRepositories(config)
            .onSuccess { repositories ->
                _state.value = _state.value.copy(
                    isLoadingRepositories = false,
                    repositories = sortWithFilteredFirst(repositories, filters.repositoryFilter) { it.name },
                    loadRepositoriesError = null
                )
            }
            .onFailure { error ->
                _state.value = _state.value.copy(
                    isLoadingRepositories = false,
                    repositories = emptyList(),
                    loadRepositoriesError = "Failed to load repositories: ${error.message}"
                )
            }
    }

    private suspend fun loadPipelines(
        config: AzureDevOpsConfig,
        filters: AzureDevOpsPreferencesFilter
    ) {
        _state.value = _state.value.copy(isLoadingPipelines = true, loadPipelinesError = null)

        azureDevOpsApi.getPipelines(config)
            .onSuccess { pipelines ->
                val selectedId = _state.value.selectedPipelineId
                val nextSelectedId = when {
                    selectedId != null && pipelines.any { it.id == selectedId } -> selectedId
                    else -> pipelines.firstOrNull()?.id
                }

                _state.value = _state.value.copy(
                    isLoadingPipelines = false,
                    pipelines = sortWithFilteredFirst(pipelines, filters.pipelineFilter) { it.name },
                    selectedPipelineId = nextSelectedId,
                    loadPipelinesError = null
                )
            }
            .onFailure { error ->
                _state.value = _state.value.copy(
                    isLoadingPipelines = false,
                    pipelines = emptyList(),
                    loadPipelinesError = "Failed to load pipelines: ${error.message}"
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

        if (s.selectedRepositoryId.isNullOrBlank()) {
            _state.value = _state.value.copy(repositoryIdError = "Repository ID cannot be empty")
            isValid = false
        } else {
            _state.value = _state.value.copy(repositoryIdError = null)
        }

        if (s.selectedBranchId.isNullOrBlank()) {
            _state.value = _state.value.copy(branchNameError = "Branch must be selected")
            isValid = false
        } else {
            _state.value = _state.value.copy(branchNameError = null)
        }

        if (s.selectedPipelineId.isNullOrBlank()) {
            _state.value = _state.value.copy(pipelineIdError = "Pipeline must be selected")
            isValid = false
        } else {
            _state.value = _state.value.copy(pipelineIdError = null)
        }

        return isValid
    }

    private fun <T> sortWithFilteredFirst(
        items: List<T>,
        filters: List<String>,
        selector: (T) -> String
    ): List<T> {
        if (filters.isEmpty()) {
            return items.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, selector))
        }

        val filtered = items.filter { item ->
            filters.any { filter ->
                selector(item).contains(filter, ignoreCase = true)
            }
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, selector))

        val remaining = items.filterNot { item ->
            filters.any { filter ->
                selector(item).contains(filter, ignoreCase = true)
            }
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, selector))

        return filtered + remaining
    }

    data class UIState(
        val isLoading: Boolean = false,
        val isConfigurationValid: Boolean = true,
        val repositories: List<AzureRepository> = emptyList(),
        val branches: List<AzureBranch> = emptyList(),

        val versionName: String = "",
        val versionNameError: String? = null,

        val buildNumber: String = "",
        val buildNumberError: String? = null,

        val releaseNotes: String = "",
        val releaseNotesError: String? = null,

        val isLoadingRepositories: Boolean = false,
        val selectedRepositoryId: String? = null,
        val repositoryIdError: String? = null,
        val loadRepositoriesError: String? = null,

        val isLoadingBranches: Boolean = false,
        val selectedBranchId: String? = null,
        val branchNameError: String? = null,
        val loadBranchesError: String? = null,

        val isLoadingPipelines: Boolean = false,
        val pipelines: List<AzurePipeline> = emptyList(),
        val selectedPipelineId: String? = null,
        val pipelineIdError: String? = null,
        val loadPipelinesError: String? = null,

        val successMessage: String? = null,
        val generalError: String? = null
    )
}