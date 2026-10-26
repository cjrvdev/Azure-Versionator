package dev.cjrv.azureversionator.ui.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.cjrv.azureversionator.data.model.app.Profile
import dev.cjrv.azureversionator.data.model.azure.AzureBranch
import dev.cjrv.azureversionator.data.model.azure.AzureBranchStats
import dev.cjrv.azureversionator.data.model.azure.AzureRepository
import dev.cjrv.azureversionator.data.network.AzureDevOpsApi
import dev.cjrv.azureversionator.data.settings.AzureSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MergeBranchesViewModel(
    private val settingsRepository: AzureSettingsRepository,
    private val azureDevOpsApi: AzureDevOpsApi
) : ViewModel() {

    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = settingsRepository.getActiveProfile()

            val isConfigValid = profile.organizationName.isNotBlank() &&
                    profile.teamProjectName.isNotBlank() &&
                    profile.personalAccessToken.isNotBlank()

            if (!isConfigValid) {
                _state.update {
                    it.copy(
                        isConfigurationValid = false,
                        generalError = "Azure DevOps configuration is incomplete. Please configure settings first."
                    )
                }
                return@launch
            }

            _state.update { it.copy(selectedProfile = profile, isLoading = true) }
            azureDevOpsApi.getRepositories(profile)
                .onSuccess { repositories ->
                    val priority = profile.filters.repositoryFilter
                    _state.update {
                        it.copy(
                            isLoading = false,
                            repositories = repositories.sortedWith(
                                compareBy<AzureRepository>(
                                    { repo -> priority.none { filter -> repo.name.contains(filter, ignoreCase = true) } },
                                    { repo -> repo.name.lowercase() }
                                )
                            )
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false, generalError = "Failed to load repositories: ${error.message}")
                    }
                }
        }
    }

    fun onRepositorySelected(repository: AzureRepository) {
        _state.update {
            it.copy(
                selectedRepositoryId = repository.id,
                defaultBranchName = repository.defaultBranch?.removePrefix("refs/heads/"),
                sourceBranchName = null,
                sourceBranches = emptyList(),
                branchStats = emptyList(),
                selectedBranches = emptySet(),
                results = emptyMap(),
                isLoadingBranches = true
            )
        }

        viewModelScope.launch {
            val profile = _state.value.selectedProfile ?: return@launch
            azureDevOpsApi.getBranches(repository.id, profile)
                .onSuccess { branches ->
                    val filters = profile.filters
                    val sorted = branches
                        .filterNot { branch -> filters.excludeBranchFilter.any { branch.name.contains(it, ignoreCase = true) } }
                        .sortedWith(
                            compareBy<AzureBranch>(
                                { branch -> filters.branchFilter.none { branch.name.contains(it, ignoreCase = true) } },
                                { branch -> branch.name.lowercase() }
                            )
                        )
                    _state.update { it.copy(isLoadingBranches = false, sourceBranches = sorted) }

                    val defaultBranch = _state.value.defaultBranchName
                    sorted.firstOrNull { it.name == defaultBranch }?.let(::onSourceBranchSelected)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoadingBranches = false, generalError = "Failed to load branches: ${error.message}")
                    }
                }
        }
    }

    fun onSourceBranchSelected(branch: AzureBranch) {
        _state.update {
            it.copy(
                sourceBranchName = branch.name,
                branchStats = emptyList(),
                selectedBranches = emptySet(),
                results = emptyMap()
            )
        }
        refresh()
    }

    fun onFilterChanged(value: String) = _state.update { it.copy(filterText = value) }

    fun onBranchToggled(branchName: String, selected: Boolean) {
        _state.update {
            it.copy(
                selectedBranches = if (selected) it.selectedBranches + branchName else it.selectedBranches - branchName
            )
        }
    }

    fun refresh() {
        if (_state.value.isMerging) return
        _state.update { it.copy(results = emptyMap()) }
        viewModelScope.launch { loadStats() }
    }

    fun mergeSelected() {
        val current = _state.value
        if (current.isMerging || current.isLoadingStats || current.selectedBranches.isEmpty()) return
        val repositoryId = current.selectedRepositoryId ?: return
        val sourceName = current.sourceBranchName ?: return
        val profile = current.selectedProfile ?: return

        viewModelScope.launch {
            _state.update { it.copy(isMerging = true, results = emptyMap()) }

            // Fresh data so merges use the latest commits.
            val stats = loadStats() ?: run {
                _state.update { it.copy(isMerging = false) }
                return@launch
            }
            val source = stats.firstOrNull { it.isBaseVersion }
            if (source == null) {
                _state.update { it.copy(isMerging = false, generalError = "Source branch '$sourceName' not found") }
                return@launch
            }

            val targets = _state.value.selectedBranches.toList().sortedBy { it.lowercase() }
            for (targetName in targets) {
                val target = stats.firstOrNull { it.name == targetName && !it.isBaseVersion }
                when {
                    target == null -> setResult(targetName, MergeStatus.Failed, "Branch no longer exists")
                    target.behindCount == 0 -> setResult(targetName, MergeStatus.UpToDate)
                    else -> {
                        setResult(targetName, MergeStatus.InProgress)
                        azureDevOpsApi.mergeIntoBranch(
                            repositoryId = repositoryId,
                            targetBranchName = target.name,
                            targetCommitId = target.commitId,
                            sourceCommitId = source.commitId,
                            comment = "Merge branch '$sourceName' into '${target.name}'",
                            selectedProfile = profile
                        )
                            .onSuccess { setResult(targetName, MergeStatus.Merged) }
                            .onFailure { setResult(targetName, MergeStatus.Failed, it.message) }
                    }
                }
            }

            _state.update { current ->
                val completed = current.results.filterValues {
                    it.status == MergeStatus.Merged || it.status == MergeStatus.UpToDate
                }.keys
                current.copy(selectedBranches = current.selectedBranches - completed)
            }
            loadStats()
            _state.update { it.copy(isMerging = false) }
        }
    }

    fun onErrorConsumed() = _state.update { it.copy(generalError = null) }

    private fun setResult(branchName: String, status: MergeStatus, message: String? = null) {
        _state.update { it.copy(results = it.results + (branchName to BranchMergeResult(status, message))) }
    }

    private suspend fun loadStats(): List<AzureBranchStats>? {
        val current = _state.value
        val repositoryId = current.selectedRepositoryId ?: return null
        val sourceName = current.sourceBranchName ?: return null
        val profile = current.selectedProfile ?: return null

        _state.update { it.copy(isLoadingStats = true) }
        return azureDevOpsApi.getBranchStats(repositoryId, sourceName, profile)
            .onSuccess { stats ->
                val targets = stats.filterNot { it.isBaseVersion }
                _state.update {
                    it.copy(
                        isLoadingStats = false,
                        branchStats = sortTargets(targets, profile),
                        selectedBranches = it.selectedBranches.filter { name -> targets.any { t -> t.name == name } }.toSet()
                    )
                }
            }
            .onFailure { error ->
                _state.update {
                    it.copy(isLoadingStats = false, generalError = "Failed to load branch status: ${error.message}")
                }
            }
            .getOrNull()
    }

    private fun sortTargets(targets: List<AzureBranchStats>, profile: Profile): List<AzureBranchStats> {
        val priority = profile.filters.branchFilter
        val excluded = profile.filters.excludeBranchFilter
        return targets
            .filterNot { branch -> excluded.any { branch.name.contains(it, ignoreCase = true) } }
            .sortedWith(
                compareBy<AzureBranchStats>(
                    { branch -> priority.none { branch.name.contains(it, ignoreCase = true) } },
                    { branch -> branch.name.lowercase() }
                )
            )
    }

    enum class MergeStatus { InProgress, Merged, UpToDate, Failed }

    data class BranchMergeResult(val status: MergeStatus, val message: String? = null)

    data class UIState(
        val isLoading: Boolean = false,
        val isConfigurationValid: Boolean = true,
        val selectedProfile: Profile? = null,

        val repositories: List<AzureRepository> = emptyList(),
        val selectedRepositoryId: String? = null,
        val defaultBranchName: String? = null,

        val isLoadingBranches: Boolean = false,
        val sourceBranches: List<AzureBranch> = emptyList(),
        val sourceBranchName: String? = null,

        val isLoadingStats: Boolean = false,
        val branchStats: List<AzureBranchStats> = emptyList(),
        val filterText: String = "",
        val selectedBranches: Set<String> = emptySet(),

        val isMerging: Boolean = false,
        val results: Map<String, BranchMergeResult> = emptyMap(),

        val generalError: String? = null
    )
}
