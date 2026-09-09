package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.add_variable
import azureversionator.shared.generated.resources.azure_connection_settings
import azureversionator.shared.generated.resources.azure_organization
import azureversionator.shared.generated.resources.azure_organization_placeholder
import azureversionator.shared.generated.resources.azure_pat
import azureversionator.shared.generated.resources.azure_pat_help
import azureversionator.shared.generated.resources.azure_pat_placeholder
import azureversionator.shared.generated.resources.azure_project_name
import azureversionator.shared.generated.resources.azure_project_name_placeholder
import azureversionator.shared.generated.resources.delete
import azureversionator.shared.generated.resources.edit_profile
import azureversionator.shared.generated.resources.edit_profile_validation_error
import azureversionator.shared.generated.resources.filter_preferences
import azureversionator.shared.generated.resources.filter_preferences_branch
import azureversionator.shared.generated.resources.filter_preferences_branch_help
import azureversionator.shared.generated.resources.filter_preferences_pipeline
import azureversionator.shared.generated.resources.filter_preferences_pipeline_help
import azureversionator.shared.generated.resources.filter_preferences_repository
import azureversionator.shared.generated.resources.filter_preferences_repository_help
import azureversionator.shared.generated.resources.input_textfield_type
import azureversionator.shared.generated.resources.ok
import azureversionator.shared.generated.resources.profile_name
import azureversionator.shared.generated.resources.profile_name_placeholder
import azureversionator.shared.generated.resources.profile_saved
import azureversionator.shared.generated.resources.remove_profile
import azureversionator.shared.generated.resources.remove_profile_confirmation_message
import azureversionator.shared.generated.resources.remove_variable
import azureversionator.shared.generated.resources.required
import azureversionator.shared.generated.resources.return_text
import azureversionator.shared.generated.resources.save_changes
import azureversionator.shared.generated.resources.value_cannot_be_empty
import azureversionator.shared.generated.resources.variable_default_value
import azureversionator.shared.generated.resources.variable_is_secret
import azureversionator.shared.generated.resources.variable_name
import azureversionator.shared.generated.resources.variables
import dev.cjrv.azureversionator.data.model.azure.AzureVariable
import dev.cjrv.azureversionator.data.model.azure.TextFieldType
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.theme.MarginSmall
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomDropdownField
import dev.cjrv.azureversionator.ui.composables.CustomPrimaryButton
import dev.cjrv.azureversionator.ui.composables.CustomPrimaryCompactButton
import dev.cjrv.azureversionator.ui.composables.CustomTextField
import dev.cjrv.azureversionator.ui.composables.CustomTextFieldWithHelp
import dev.cjrv.azureversionator.ui.composables.ExpandableSection
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun EditProfileScreen(onNavigateBack: () -> Unit) {
    val vm = koinViewModel<EditProfileViewModel>()
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showRemoveProfileConfirmation by remember { mutableStateOf(false) }
    val saveSuccessMessage = stringResource(Res.string.profile_saved)
    val validationErrorMessage = stringResource(Res.string.edit_profile_validation_error)

    LaunchedEffect(state.saveFeedback) {
        when (state.saveFeedback) {
            EditProfileViewModel.SaveFeedback.Saved -> {
                snackbarHostState.showSnackbar(saveSuccessMessage)
                vm.onSaveFeedbackConsumed()
            }

            EditProfileViewModel.SaveFeedback.ValidationError -> {
                snackbarHostState.showSnackbar(validationErrorMessage)
                vm.onSaveFeedbackConsumed()
            }

            null -> Unit
        }
    }

    if (showRemoveProfileConfirmation) {
        RemoveProfileConfirmationDialog(
            onDismiss = { showRemoveProfileConfirmation = false },
            onConfirm = {
                showRemoveProfileConfirmation = false
                vm.removeProfile()
                onNavigateBack()
            }
        )
    }

    Screen {
        Scaffold(
            topBar = {
                TopAppBar(
                    stringResource(Res.string.edit_profile),
                    hasBackButton = true,
                    onBackPressed = { onNavigateBack() }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(innerPadding)
            ) {
                if (state.isLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        InfiniteLoadingIndicator()
                    }
                } else {
                    LazyColumn(
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
                    ) {
                        item {
                            ProfileNameOrRemove(
                                profileName = state.profileName,
                                onProfileNameChanged = vm::onProfileNameChanged,
                                onRemoveProfileClick = { showRemoveProfileConfirmation = true }
                            )
                        }
                        item {
                            ConnectionSettings(
                                state = state,
                                onTeamProjectNameChanged = vm::onTeamProjectNameChanged,
                                onPersonalAccessTokenChange = vm::onPersonalAccessTokenChange,
                                onOrganizationChange = vm::onOrganizationChange
                            )
                        }
                        item {
                            VariablesSection(
                                state = state,
                                onAddNewVariable = vm::addNewVariable,
                                onVariableNameChanged = vm::onVariableNameChanged,
                                onVariableValueChanged = vm::onVariableValueChanged,
                                onVariableSecretChanged = vm::onVariableSecretChanged,
                                onVariableTextFieldTypeChanged = vm::onVariableTextFieldTypeChanged,
                                onRemoveVariable = vm::removeVariable,
                                onVariableRequiredChanged = vm::onVariableRequiredChanged
                            )
                        }
                        item {
                            FilterPreferencesSection(
                                state = state,
                                onPipelineFilterChange = vm::onPipelineFilterChanged,
                                onRepositoryFilterChange = vm::onRepositoryFilterChanged,
                                onBranchFilterChange = vm::onBranchFilterChanged
                            )
                        }
                        item {
                            CustomPrimaryButton(
                                text = stringResource(Res.string.save_changes),
                                onClick = vm::onSaveChangesClicked,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = MarginMedium)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileNameOrRemove(
    profileName: String,
    onProfileNameChanged: (String) -> Unit,
    onRemoveProfileClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CustomTextField(
            label = stringResource(Res.string.profile_name),
            value = profileName,
            onValueChange = onProfileNameChanged,
            modifier = Modifier.weight(1f),
            placeholder = stringResource(Res.string.profile_name_placeholder),
            imeAction = ImeAction.Next
        )
        Spacer(modifier = Modifier.width(MarginSmall))
        IconButton(onClick = { onRemoveProfileClick() }) {
            Icon(
                imageVector = vectorResource(Res.drawable.delete),
                contentDescription = stringResource(Res.string.remove_profile),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun RemoveProfileConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(4.dp),
        title = { Text(stringResource(Res.string.remove_profile)) },
        text = { Text(stringResource(Res.string.remove_profile_confirmation_message)) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.return_text))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.ok))
            }
        }
    )
}

@Composable
fun VariablesSection(
    state: EditProfileViewModel.UIState,
    onAddNewVariable: () -> Unit,
    onVariableNameChanged: (Int, String) -> Unit,
    onVariableValueChanged: (Int, String) -> Unit,
    onVariableSecretChanged: (Int, Boolean) -> Unit,
    onVariableTextFieldTypeChanged: (Int, TextFieldType) -> Unit,
    onRemoveVariable: (Int) -> Unit,
    onVariableRequiredChanged: (Int, Boolean) -> Unit
) {
    ExpandableSection(
        title = {
            Text(
                text = stringResource(Res.string.variables).uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MarginSmall),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MarginSmall)
        ) {
            state.variables.forEachIndexed { index, variable ->
                VariableRow(
                    variable = variable,
                    onNameChange = { onVariableNameChanged(index, it) },
                    onValueChange = { onVariableValueChanged(index, it) },
                    onSecretChange = { onVariableSecretChanged(index, it) },
                    onRequiredChange = { onVariableRequiredChanged(index, it) },
                    onVariableTextFieldTypeChanged = { onVariableTextFieldTypeChanged(index, it) },
                    onRemove = { onRemoveVariable(index) },
                    showValidationErrors = state.showValidationErrors,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            CustomPrimaryCompactButton(
                text = stringResource(Res.string.add_variable),
                onClick = onAddNewVariable,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = MarginSmall)
            )
        }
    }
}

@Composable
fun VariableRow(
    variable: AzureVariable,
    onNameChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onSecretChange: (Boolean) -> Unit,
    onRequiredChange: (Boolean) -> Unit,
    onVariableTextFieldTypeChanged: (TextFieldType) -> Unit,
    onRemove: () -> Unit,
    showValidationErrors: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(MarginSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.delete),
                    contentDescription = stringResource(Res.string.remove_variable),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.width(MarginSmall))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    CustomTextField(
                        label = stringResource(Res.string.variable_name),
                        value = variable.name,
                        onValueChange = onNameChange,
                        modifier = Modifier.weight(1f),
                        isError = showValidationErrors && variable.name.isBlank(),
                        errorMessage = stringResource(Res.string.value_cannot_be_empty),
                        imeAction = ImeAction.Next,
                    )
                    Spacer(modifier = Modifier.width(MarginSmall))
                    CustomDropdownField(
                        label = stringResource(Res.string.input_textfield_type),
                        selectedItem = variable.textFieldType,
                        options = TextFieldType.entries,
                        modifier = Modifier.weight(1f),
                        optionLabel = { it.name },
                        onOptionSelected = onVariableTextFieldTypeChanged
                    )
                    Spacer(modifier = Modifier.width(MarginSmall))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = variable.isRequired, onCheckedChange = onRequiredChange)
                        Text(
                            text = stringResource(Res.string.required).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(MarginSmall))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    CustomTextField(
                        label = stringResource(Res.string.variable_default_value),
                        value = variable.value,
                        onValueChange = onValueChange,
                        modifier = Modifier.weight(1f),
                        isPassword = variable.isSecret,
                        imeAction = ImeAction.Next,
                    )
                    Spacer(modifier = Modifier.width(MarginSmall))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = variable.isSecret, onCheckedChange = onSecretChange)
                        Text(
                            text = stringResource(Res.string.variable_is_secret).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectionSettings(
    state: EditProfileViewModel.UIState,
    onPersonalAccessTokenChange: (String) -> Unit,
    onOrganizationChange: (String) -> Unit,
    onTeamProjectNameChanged: (String) -> Unit
) {
    ExpandableSection(
        title = {
            Text(
                text = stringResource(Res.string.azure_connection_settings).uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MarginMedium),
            modifier = Modifier.padding(
                start = MarginMedium,
                end = MarginMedium,
                bottom = MarginMedium
            )
        ) {
            CustomTextFieldWithHelp(
                label = stringResource(Res.string.azure_pat),
                value = state.personalAccessToken,
                helpText = stringResource(Res.string.azure_pat_help),
                onValueChange = onPersonalAccessTokenChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.azure_pat_placeholder),
                isPassword = true,
                isError = state.personalAccessTokenError,
                errorMessage = stringResource(Res.string.value_cannot_be_empty),
                imeAction = ImeAction.Next,
            )

            CustomTextField(
                label = stringResource(Res.string.azure_organization),
                value = state.organizationName,
                onValueChange = onOrganizationChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.azure_organization_placeholder),
                isError = state.organizationNameError,
                errorMessage = stringResource(Res.string.value_cannot_be_empty),
                imeAction = ImeAction.Next
            )

            CustomTextFieldWithHelp(
                label = stringResource(Res.string.azure_project_name),
                value = state.teamProjectName,
                helpText = stringResource(Res.string.azure_project_name_placeholder),
                onValueChange = { onTeamProjectNameChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.azure_project_name_placeholder),
                isError = state.teamProjectNameError,
                errorMessage = stringResource(Res.string.value_cannot_be_empty),
                imeAction = ImeAction.Done,
            )
        }
    }
}

@Composable
private fun FilterPreferencesSection(
    state: EditProfileViewModel.UIState,
    onPipelineFilterChange: (String) -> Unit,
    onRepositoryFilterChange: (String) -> Unit,
    onBranchFilterChange: (String) -> Unit
) {
    ExpandableSection(
        title = {
            Text(
                text = stringResource(Res.string.filter_preferences).uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MarginMedium),
            modifier = Modifier.padding(
                start = MarginMedium,
                end = MarginMedium,
                bottom = MarginMedium
            )
        ) {
            CustomTextFieldWithHelp(
                label = stringResource(Res.string.filter_preferences_pipeline),
                value = state.pipelineFilter,
                helpText = stringResource(Res.string.filter_preferences_pipeline_help),
                onValueChange = onPipelineFilterChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.filter_preferences_pipeline),
                imeAction = ImeAction.Next
            )

            CustomTextFieldWithHelp(
                label = stringResource(Res.string.filter_preferences_repository),
                value = state.repositoryFilter,
                helpText = stringResource(Res.string.filter_preferences_repository_help),
                onValueChange = onRepositoryFilterChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.filter_preferences_repository),
                imeAction = ImeAction.Next
            )

            CustomTextFieldWithHelp(
                label = stringResource(Res.string.filter_preferences_branch),
                value = state.branchFilter,
                helpText = stringResource(Res.string.filter_preferences_branch_help),
                onValueChange = onBranchFilterChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(Res.string.filter_preferences_branch),
                imeAction = ImeAction.Done
            )
        }
    }
}