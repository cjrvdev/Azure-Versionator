package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.model.app.Profile
import dev.cjrv.azureversionator.data.model.azure.AzureBranch
import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsPreferencesFilter
import dev.cjrv.azureversionator.data.model.azure.AzurePipeline
import dev.cjrv.azureversionator.data.model.azure.AzureRepository
import dev.cjrv.azureversionator.data.model.azure.AzureVariable
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
            val selectedProfile = settingsRepository.getActiveProfile()

            val isConfigValid = selectedProfile.organizationName.isNotBlank() && selectedProfile.teamProjectName.isNotBlank() &&
                    selectedProfile.personalAccessToken.isNotBlank()

            if (!isConfigValid) {
                _state.value = _state.value.copy(
                    isConfigurationValid = false,
                    generalError = "Azure DevOps configuration is incomplete. Please configure settings first."
                )
            } else {
                _state.value = _state.value.copy(isConfigurationValid = true, isLoading = true, selectedProfile = selectedProfile)
                loadRepositories(selectedProfile.filters)
                loadPipelines(selectedProfile.filters)
                _state.value = _state.value.copy(isLoading = false)
            }
        }
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

    fun onVariableValueChanged(index: Int, newValue: String) {
        _state.update { current ->
            val selectedProfile = current.selectedProfile ?: return@update current
            if (index !in selectedProfile.variables.indices) {
                current
            } else {
                current.copy(
                    selectedProfile = selectedProfile.copy(
                        variables = selectedProfile.variables.updateVariable(index) { variable ->
                            variable.copy(value = newValue)
                        }
                    )
                )
            }
        }
    }

    fun createVersion() {
        if (!_state.value.isConfigurationValid) return
        if (!validate()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, generalError = null)

            try {
                val result = azureDevOpsApi.runPipeline(
                    selectedProfile = _state.value.selectedProfile!!,
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

            val selectedProfile = _state.value.selectedProfile!!
            azureDevOpsApi.getBranches(repositoryId, selectedProfile)
                .onSuccess { branches ->
                    _state.value = _state.value.copy(
                        isLoadingBranches = false,
                        branches = sortWithFilteredFirst(branches, selectedProfile.filters.branchFilter) { it.name },
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
        filters: AzureDevOpsPreferencesFilter
    ) {
        _state.value = _state.value.copy(isLoadingRepositories = true, loadRepositoriesError = null)

        azureDevOpsApi.getRepositories(_state.value.selectedProfile!!)
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
        filters: AzureDevOpsPreferencesFilter
    ) {
        _state.value = _state.value.copy(isLoadingPipelines = true, loadPipelinesError = null)

        azureDevOpsApi.getPipelines(_state.value.selectedProfile!!)
            .onSuccess { pipelines ->
                val sortedPipelines = sortWithFilteredFirst(pipelines, filters.pipelineFilter) { it.name }
                val nextSelectedId = sortedPipelines.firstOrNull()?.id

                _state.value = _state.value.copy(
                    isLoadingPipelines = false,
                    pipelines = sortedPipelines,
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

        if (s.selectedProfile?.variables?.any { it.isRequired && it.value.isBlank() } == true) {
            isValid = false
        }

        _state.value = _state.value.copy(showValidationErrors = true)

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

    private fun List<AzureVariable>.updateVariable(
        index: Int,
        transform: (AzureVariable) -> AzureVariable
    ): List<AzureVariable> = mapIndexed { variableIndex, variable ->
        if (variableIndex == index) {
            transform(variable)
        } else {
            variable
        }
    }

    data class UIState(
        val isLoading: Boolean = false,
        val isConfigurationValid: Boolean = true,
        val repositories: List<AzureRepository> = emptyList(),
        val branches: List<AzureBranch> = emptyList(),
        val selectedProfile : Profile? = null,

        //Repos
        val isLoadingRepositories: Boolean = false,
        val selectedRepositoryId: String? = null,
        val repositoryIdError: String? = null,
        val loadRepositoriesError: String? = null,

        //Branches
        val isLoadingBranches: Boolean = false,
        val selectedBranchId: String? = null,
        val branchNameError: String? = null,
        val loadBranchesError: String? = null,

        //Pipelines
        val isLoadingPipelines: Boolean = false,
        val pipelines: List<AzurePipeline> = emptyList(),
        val selectedPipelineId: String? = null,
        val pipelineIdError: String? = null,
        val loadPipelinesError: String? = null,

        val showValidationErrors: Boolean = false,
        val successMessage: String? = null,
        val generalError: String? = null
    )
}