package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import azureversionator.shared.generated.resources.azure_branch_name
import azureversionator.shared.generated.resources.azure_branch_name_placeholder
import azureversionator.shared.generated.resources.azure_pipeline_id
import azureversionator.shared.generated.resources.azure_pipeline_id_placeholder
import azureversionator.shared.generated.resources.azure_repository_name
import azureversionator.shared.generated.resources.azure_repository_name_placeholder
import azureversionator.shared.generated.resources.new_version
import azureversionator.shared.generated.resources.new_version_submit
import azureversionator.shared.generated.resources.ok
import azureversionator.shared.generated.resources.return_text
import azureversionator.shared.generated.resources.upload
import azureversionator.shared.generated.resources.value_cannot_be_empty
import azureversionator.shared.generated.resources.variables
import dev.cjrv.azureversionator.data.model.azure.AzureVariable
import dev.cjrv.azureversionator.data.model.azure.TextFieldType
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomDropdownField
import dev.cjrv.azureversionator.ui.composables.CustomMultilineTextField
import dev.cjrv.azureversionator.ui.composables.CustomPrimaryButton
import dev.cjrv.azureversionator.ui.composables.CustomTextField
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
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
            dismissButton = {
                TextButton(onClick = {
                    vm.onSuccessMessageConsumed()
                }) {
                    Text(stringResource(Res.string.return_text))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.onSuccessMessageConsumed()
                    onNavigateBack()
                }) {
                    Text(stringResource(Res.string.ok))
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
                    Text(stringResource(Res.string.ok))
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
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(MarginMedium)
                            .verticalScroll(rememberScrollState())
                    ) {
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
                                errorMessage = state.repositoryIdError
                                    ?: state.loadRepositoriesError
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

                        ProfileVariablesSection(
                            variables = state.selectedProfile?.variables.orEmpty(),
                            showValidationErrors = state.showValidationErrors,
                            onVariableValueChanged = vm::onVariableValueChanged
                        )

                        CustomPrimaryButton(
                            text = stringResource(Res.string.new_version_submit),
                            onClick = vm::createVersion,
                            enabled = state.isConfigurationValid,
                            modifier = Modifier
                                .fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.upload),
                                    contentDescription = stringResource(Res.string.new_version_submit)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileVariablesSection(
    variables: List<AzureVariable>,
    showValidationErrors: Boolean,
    onVariableValueChanged: (Int, String) -> Unit
) {
    if (variables.isEmpty()) return

    val singleLineVariables = variables.withIndex()
        .filter { it.value.textFieldType == TextFieldType.SingleLine }
        .chunked(2)
    val multilineVariables = variables.withIndex()
        .filter { it.value.textFieldType == TextFieldType.Multiline }

    Text(
        text = stringResource(Res.string.variables),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = MarginMedium)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(MarginMedium),
        modifier = Modifier.fillMaxWidth()
    ) {
        singleLineVariables.forEach { variablePair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(MarginMedium),
                modifier = Modifier.fillMaxWidth()
            ) {
                variablePair.forEach { indexedVariable ->
                    VariableField(
                        variable = indexedVariable.value,
                        showValidationErrors = showValidationErrors,
                        onValueChange = { value ->
                            onVariableValueChanged(
                                indexedVariable.index,
                                value
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (variablePair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        multilineVariables.forEach { indexedVariable ->
            VariableField(
                variable = indexedVariable.value,
                showValidationErrors = showValidationErrors,
                onValueChange = { value -> onVariableValueChanged(indexedVariable.index, value) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
            )
        }
    }
}

@Composable
private fun VariableField(
    variable: AzureVariable,
    showValidationErrors: Boolean,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isError = showValidationErrors && variable.isRequired && variable.value.isBlank()
    val errorMessage = if (isError) stringResource(Res.string.value_cannot_be_empty) else null

    if (variable.textFieldType == TextFieldType.Multiline) {
        CustomMultilineTextField(
            label = variable.name,
            value = variable.value,
            onValueChange = onValueChange,
            modifier = modifier,
            isPassword = variable.isSecret,
            isError = isError,
            errorMessage = errorMessage
        )
    } else {
        CustomTextField(
            label = variable.name,
            value = variable.value,
            onValueChange = onValueChange,
            modifier = modifier,
            isPassword = variable.isSecret,
            isError = isError,
            errorMessage = errorMessage,
            imeAction = ImeAction.Next
        )
    }
}