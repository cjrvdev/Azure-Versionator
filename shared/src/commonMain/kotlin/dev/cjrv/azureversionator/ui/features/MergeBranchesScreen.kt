package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.add_task
import azureversionator.shared.generated.resources.azure_branch_name_placeholder
import azureversionator.shared.generated.resources.azure_repository_name
import azureversionator.shared.generated.resources.azure_repository_name_placeholder
import azureversionator.shared.generated.resources.error
import azureversionator.shared.generated.resources.merge_branches
import azureversionator.shared.generated.resources.merge_filter_branches
import azureversionator.shared.generated.resources.merge_no_branches
import azureversionator.shared.generated.resources.merge_pending_commits
import azureversionator.shared.generated.resources.merge_refresh
import azureversionator.shared.generated.resources.merge_source_branch
import azureversionator.shared.generated.resources.merge_status_failed
import azureversionator.shared.generated.resources.merge_status_merged
import azureversionator.shared.generated.resources.merge_status_merging
import azureversionator.shared.generated.resources.merge_submit
import azureversionator.shared.generated.resources.merge_target_branches
import azureversionator.shared.generated.resources.merge_up_to_date
import azureversionator.shared.generated.resources.ok
import azureversionator.shared.generated.resources.sync
import dev.cjrv.azureversionator.data.model.azure.AzureBranchStats
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomPrimaryButton
import dev.cjrv.azureversionator.ui.composables.CustomSearchableDropdownField
import dev.cjrv.azureversionator.ui.composables.CustomSecondaryCompactButton
import dev.cjrv.azureversionator.ui.composables.CustomTextField
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TechLabel
import dev.cjrv.azureversionator.ui.composables.TechPanel
import dev.cjrv.azureversionator.ui.composables.TechToggleChip
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MergeBranchesScreen(onNavigateBack: () -> Unit) {
    val vm = koinViewModel<MergeBranchesViewModel>()
    val state by vm.state.collectAsState()
    val selectedRepository = state.repositories.firstOrNull { it.id == state.selectedRepositoryId }
    val sourceBranch = state.sourceBranches.firstOrNull { it.name == state.sourceBranchName }
    val visibleBranches = state.branchStats.filter { it.name.contains(state.filterText, ignoreCase = true) }
    val busy = state.isLoadingStats || state.isMerging

    if (state.generalError != null) {
        AlertDialog(
            onDismissRequest = { vm.onErrorConsumed() },
            shape = RoundedCornerShape(4.dp),
            title = { TechLabel(text = stringResource(Res.string.error)) },
            text = { Text(state.generalError.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { vm.onErrorConsumed() }) {
                    TechLabel(text = stringResource(Res.string.ok))
                }
            }
        )
    }

    Screen {
        Scaffold(topBar = {
            TopAppBar(
                stringResource(Res.string.merge_branches),
                hasBackButton = true,
                onBackPressed = { onNavigateBack() }
            )
        }) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                if (state.isLoading) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        InfiniteLoadingIndicator()
                    }
                } else {
                    TechPanel(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MarginMedium)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(MarginMedium),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(MarginMedium)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(MarginMedium)) {
                                CustomSearchableDropdownField(
                                    label = stringResource(Res.string.azure_repository_name),
                                    selectedItem = selectedRepository,
                                    options = state.repositories,
                                    optionLabel = { it.name },
                                    onOptionSelected = vm::onRepositorySelected,
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                    placeholder = stringResource(Res.string.azure_repository_name_placeholder),
                                    enabled = state.isConfigurationValid && !state.isMerging
                                )
                                CustomSearchableDropdownField(
                                    label = stringResource(Res.string.merge_source_branch),
                                    selectedItem = sourceBranch,
                                    options = state.sourceBranches,
                                    optionLabel = { it.name },
                                    onOptionSelected = vm::onSourceBranchSelected,
                                    modifier = Modifier.fillMaxWidth().weight(1f),
                                    placeholder = stringResource(Res.string.azure_branch_name_placeholder),
                                    enabled = state.selectedRepositoryId != null &&
                                            !state.isLoadingBranches && !state.isMerging
                                )
                            }

                            if (state.sourceBranchName != null) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(MarginMedium),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CustomTextField(
                                        label = stringResource(Res.string.merge_filter_branches),
                                        value = state.filterText,
                                        onValueChange = vm::onFilterChanged,
                                        modifier = Modifier.weight(1f),
                                        imeAction = ImeAction.Done,
                                        ignoreCase = true
                                    )
                                    CustomSecondaryCompactButton(
                                        text = stringResource(Res.string.merge_refresh),
                                        onClick = vm::refresh,
                                        enabled = !busy,
                                        leadingIcon = {
                                            Icon(
                                                imageVector = vectorResource(Res.drawable.sync),
                                                contentDescription = stringResource(Res.string.merge_refresh)
                                            )
                                        }
                                    )
                                }

                                TechLabel(
                                    text = stringResource(Res.string.merge_target_branches),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                    when {
                                        state.isLoadingStats && state.branchStats.isEmpty() -> Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) { InfiniteLoadingIndicator() }

                                        visibleBranches.isEmpty() -> TechLabel(
                                            text = stringResource(Res.string.merge_no_branches),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                            modifier = Modifier.align(Alignment.Center)
                                        )

                                        else -> LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            items(visibleBranches, key = { it.name }) { branch ->
                                                BranchRow(
                                                    branch = branch,
                                                    checked = branch.name in state.selectedBranches,
                                                    enabled = !state.isMerging,
                                                    result = state.results[branch.name],
                                                    onCheckedChange = { vm.onBranchToggled(branch.name, it) }
                                                )
                                            }
                                        }
                                    }
                                }

                                CustomPrimaryButton(
                                    text = stringResource(Res.string.merge_submit),
                                    onClick = vm::mergeSelected,
                                    enabled = state.selectedBranches.isNotEmpty() && !busy,
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = vectorResource(Res.drawable.add_task),
                                            contentDescription = stringResource(Res.string.merge_submit)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BranchRow(
    branch: AzureBranchStats,
    checked: Boolean,
    enabled: Boolean,
    result: MergeBranchesViewModel.BranchMergeResult?,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val (statusText, statusColor) = when (result?.status) {
        MergeBranchesViewModel.MergeStatus.InProgress ->
            stringResource(Res.string.merge_status_merging) to colors.primary

        MergeBranchesViewModel.MergeStatus.Merged ->
            stringResource(Res.string.merge_status_merged) to colors.primary

        MergeBranchesViewModel.MergeStatus.UpToDate ->
            stringResource(Res.string.merge_up_to_date) to colors.onSurface.copy(alpha = 0.6f)

        MergeBranchesViewModel.MergeStatus.Failed ->
            stringResource(Res.string.merge_status_failed, result.message.orEmpty()) to colors.error

        null -> if (branch.behindCount == 0) {
            stringResource(Res.string.merge_up_to_date) to colors.onSurface.copy(alpha = 0.6f)
        } else {
            stringResource(Res.string.merge_pending_commits, branch.behindCount) to colors.tertiary
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MarginMedium),
        modifier = Modifier.fillMaxWidth()
    ) {
        TechToggleChip(
            label = branch.name,
            checked = checked,
            onCheckedChange = { if (enabled) onCheckedChange(it) },
            modifier = Modifier.weight(1f)
        )
        TechLabel(
            text = statusText,
            color = statusColor,
            ignoreCase = true,
            modifier = Modifier.weight(1f)
        )
    }
}
