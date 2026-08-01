package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.add_variable
import azureversionator.shared.generated.resources.azure_project_name
import azureversionator.shared.generated.resources.azure_project_name_placeholder
import azureversionator.shared.generated.resources.delete
import azureversionator.shared.generated.resources.edit_profile
import azureversionator.shared.generated.resources.edit_profile_validation_error
import azureversionator.shared.generated.resources.profile_name
import azureversionator.shared.generated.resources.profile_name_placeholder
import azureversionator.shared.generated.resources.profile_saved
import azureversionator.shared.generated.resources.remove_variable
import azureversionator.shared.generated.resources.save_changes
import azureversionator.shared.generated.resources.value_cannot_be_empty
import azureversionator.shared.generated.resources.variable_is_secret
import azureversionator.shared.generated.resources.variable_name
import azureversionator.shared.generated.resources.variable_default_value
import azureversionator.shared.generated.resources.variables
import dev.cjrv.azureversionator.data.model.azure.AzureVariable
import dev.cjrv.azureversionator.theme.CornerRadius
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.theme.MarginSmall
import dev.cjrv.azureversionator.ui.Screen
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
                                MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(CornerRadius)
                            )
                            .padding(MarginMedium)
                    ) {
                        item {
                            ProfileNameField(
                                profileName = state.profileName,
                                onProfileNameChanged = vm::onProfileNameChanged
                            )
                        }
                        item {
                            TeamProjectSection(
                                state = state,
                                onTeamProjectNameChanged = vm::onTeamProjectNameChanged
                            )
                        }
                        item {
                            VariablesSection(
                                state = state,
                                onAddNewVariable = vm::addNewVariable,
                                onVariableNameChanged = vm::onVariableNameChanged,
                                onVariableValueChanged = vm::onVariableValueChanged,
                                onVariableSecretChanged = vm::onVariableSecretChanged,
                                onRemoveVariable = vm::removeVariable
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
private fun ProfileNameField(
    profileName: String,
    onProfileNameChanged: (String) -> Unit
) {
    CustomTextField(
        label = stringResource(Res.string.profile_name),
        value = profileName,
        onValueChange = onProfileNameChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = stringResource(Res.string.profile_name_placeholder),
        imeAction = ImeAction.Next
    )
}

@Composable
fun VariablesSection(
    state: EditProfileViewModel.UIState,
    onAddNewVariable: () -> Unit,
    onVariableNameChanged: (Int, String) -> Unit,
    onVariableValueChanged: (Int, String) -> Unit,
    onVariableSecretChanged: (Int, Boolean) -> Unit,
    onRemoveVariable: (Int) -> Unit
) {
    ExpandableSection(
        title = {
            Text(
                text = stringResource(Res.string.variables),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(MarginMedium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MarginMedium)
        ) {
            state.variables.forEachIndexed { index, variable ->
                VariableRow(
                    variable = variable,
                    onNameChange = { onVariableNameChanged(index, it) },
                    onValueChange = { onVariableValueChanged(index, it) },
                    onSecretChange = { onVariableSecretChanged(index, it) },
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
                    .padding(horizontal = MarginMedium, vertical = MarginMedium)
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
    onRemove: () -> Unit,
    showValidationErrors: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = onRemove
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.delete),
                contentDescription = stringResource(Res.string.remove_variable),
                tint = MaterialTheme.colorScheme.error
            )
        }
        CustomTextField(
            label = stringResource(Res.string.variable_name),
            value = variable.name,
            onValueChange = onNameChange,
            modifier = Modifier.weight(1f),
            isPassword = false,
            isError = showValidationErrors && variable.name.isBlank(),
            errorMessage = stringResource(Res.string.value_cannot_be_empty),
            imeAction = ImeAction.Next,
        )
        Spacer(modifier = Modifier.width(MarginSmall))
        CustomTextField(
            label = stringResource(Res.string.variable_default_value),
            value = variable.value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            isPassword = variable.isSecret,
            imeAction = ImeAction.Next,
        )
        Checkbox(checked = variable.isSecret, onCheckedChange = onSecretChange)
        Text(
            text = stringResource(Res.string.variable_is_secret),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun TeamProjectSection(
    state: EditProfileViewModel.UIState,
    onTeamProjectNameChanged: (String) -> Unit
) {
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