package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.azure_branch_name
import azureversionator.shared.generated.resources.azure_branch_name_placeholder
import azureversionator.shared.generated.resources.azure_devops_settings
import azureversionator.shared.generated.resources.azure_pipeline_id
import azureversionator.shared.generated.resources.azure_pipeline_id_placeholder
import azureversionator.shared.generated.resources.azure_repository_name
import azureversionator.shared.generated.resources.azure_repository_name_placeholder
import azureversionator.shared.generated.resources.new_version
import azureversionator.shared.generated.resources.new_version_settings
import azureversionator.shared.generated.resources.new_version_submit
import azureversionator.shared.generated.resources.release_notes
import azureversionator.shared.generated.resources.release_notes_build_number
import azureversionator.shared.generated.resources.release_notes_build_number_placeholder
import azureversionator.shared.generated.resources.release_notes_placeholder
import azureversionator.shared.generated.resources.release_notes_version_name
import azureversionator.shared.generated.resources.release_notes_version_name_placeholder
import dev.cjrv.azureversionator.theme.CornerRadius
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomDropdownField
import dev.cjrv.azureversionator.ui.composables.CustomMultilineTextField
import dev.cjrv.azureversionator.ui.composables.CustomPrimaryButton
import dev.cjrv.azureversionator.ui.composables.CustomTextField
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewVersionScreen(onNavigateBack: () -> Unit) {
    val vm = koinViewModel<NewVersionViewModel>()
    val state by vm.state.collectAsState()
    val selectedRepository = state.repositories.firstOrNull { it.id == state.selectedRepositoryId }
    val selectedBranch = state.branches.firstOrNull { it.fullName == state.selectedBranchId }
    val selectedPipeline = state.pipelines.firstOrNull { it.id == state.selectedPipelineId }

    // Handle success message
    if (state.successMessage != null) {
        AlertDialog(
            onDismissRequest = { vm.onSuccessMessageConsumed() },
            title = { Text("Success") },
            text = { Text(state.successMessage.orEmpty()) },
            confirmButton = {
                TextButton(onClick = {
                    vm.onSuccessMessageConsumed()
                    onNavigateBack()
                }) {
                    Text("OK")
                }
            }
        )
    }

    // Handle error message
    if (state.generalError != null) {
        AlertDialog(
            onDismissRequest = { vm.onErrorConsumed() },
            title = { Text("Error") },
            text = { Text(state.generalError.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { vm.onErrorConsumed() }) {
                    Text("OK")
                }
            }
        )
    }

    Screen {
        Scaffold(topBar = {
            TopAppBar(
                stringResource(Res.string.new_version),
                hasBackButton = true,
                onBackPressed = { onNavigateBack() }
            )
        }) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(innerPadding)
            ) {
                if (state.isLoading) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        InfiniteLoadingIndicator()
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MarginMedium),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MarginMedium)
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(CornerRadius)
                            )
                            .padding(MarginMedium)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = stringResource(Res.string.new_version_settings),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        CustomDropdownField(
                            label = stringResource(Res.string.azure_pipeline_id),
                            selectedItem = selectedPipeline,
                            options = state.pipelines,
                            optionLabel = { it.name },
                            onOptionSelected = vm::onPipelineSelected,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = stringResource(Res.string.azure_pipeline_id_placeholder),
                            enabled = state.isConfigurationValid && !state.isLoadingPipelines,
                            isError = state.pipelineIdError != null || state.loadPipelinesError != null,
                            errorMessage = state.pipelineIdError ?: state.loadPipelinesError
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(MarginMedium)) {
                            CustomDropdownField(
                                label = stringResource(Res.string.azure_repository_name),
                                selectedItem = selectedRepository,
                                options = state.repositories,
                                optionLabel = { it.name },
                                onOptionSelected = vm::onRepositorySelected,
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                placeholder = stringResource(Res.string.azure_repository_name_placeholder),
                                enabled = state.isConfigurationValid && !state.isLoadingRepositories,
                                isError = state.repositoryIdError != null || state.loadRepositoriesError != null,
                                errorMessage = state.repositoryIdError ?: state.loadRepositoriesError
                            )
                            CustomDropdownField(
                                label = stringResource(Res.string.azure_branch_name),
                                selectedItem = selectedBranch,
                                options = state.branches,
                                optionLabel = { it.name },
                                onOptionSelected = vm::onBranchSelected,
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                placeholder = stringResource(Res.string.azure_branch_name_placeholder),
                                enabled = state.selectedRepositoryId != null && !state.isLoadingBranches,
                                isError = state.branchNameError != null || state.loadBranchesError != null,
                                errorMessage = state.branchNameError ?: state.loadBranchesError
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(MarginMedium)) {
                            CustomTextField(
                                label = stringResource(Res.string.release_notes_version_name),
                                value = state.versionName,
                                onValueChange = vm::onVersionNameChange,
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                placeholder = stringResource(Res.string.release_notes_version_name_placeholder),
                                isError = state.versionNameError != null,
                                errorMessage = state.versionNameError,
                                imeAction = ImeAction.Next
                            )
                            CustomTextField(
                                label = stringResource(Res.string.release_notes_build_number),
                                value = state.buildNumber,
                                onValueChange = vm::onBuildNumberChange,
                                modifier = Modifier.fillMaxWidth().weight(1f),
                                placeholder = stringResource(Res.string.release_notes_build_number_placeholder),
                                isError = state.buildNumberError != null,
                                errorMessage = state.buildNumberError,
                                imeAction = ImeAction.Next
                            )
                        }
                        CustomMultilineTextField(
                            label = stringResource(Res.string.release_notes),
                            value = state.releaseNotes,
                            onValueChange = vm::onReleaseNotesChange,
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            placeholder = stringResource(Res.string.release_notes_placeholder),
                            isError = state.releaseNotesError != null,
                            errorMessage = state.releaseNotesError,
                        )
                        CustomPrimaryButton(
                            text = stringResource(Res.string.new_version_submit),
                            onClick = vm::createVersion,
                            enabled = state.isConfigurationValid,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}