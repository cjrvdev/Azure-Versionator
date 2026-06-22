package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.settings
import azureversionator.shared.generated.resources.azure_devops_settings
import azureversionator.shared.generated.resources.azure_organization
import azureversionator.shared.generated.resources.azure_organization_placeholder
import azureversionator.shared.generated.resources.azure_project_name
import azureversionator.shared.generated.resources.azure_project_name_placeholder
import azureversionator.shared.generated.resources.azure_pat
import azureversionator.shared.generated.resources.azure_pat_placeholder
import azureversionator.shared.generated.resources.save_settings
import azureversionator.shared.generated.resources.settings_saved
import dev.cjrv.azureversionator.theme.CornerRadius
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomTextField
import dev.cjrv.azureversionator.ui.composables.CustomPrimaryButton
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(onNavigateBack : () -> Unit) {
    val vm = koinViewModel<SettingsViewModel>()
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val settingsSavedMessage = stringResource(Res.string.settings_saved)

    LaunchedEffect(state.savedFeedback) {
        if (state.savedFeedback) {
            snackbarHostState.showSnackbar(settingsSavedMessage)
            vm.onSavedFeedbackConsumed()
        }
    }

    Screen {
        Scaffold(
            topBar = {
                TopAppBar(
                    stringResource(Res.string.settings),
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
                            text = stringResource(Res.string.azure_devops_settings),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        CustomTextField(
                            label = stringResource(Res.string.azure_pat),
                            value = state.personalAccessToken,
                            onValueChange = vm::onPersonalAccessTokenChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = stringResource(Res.string.azure_pat_placeholder),
                            isPassword = true,
                            isError = state.personalAccessTokenError != null,
                            errorMessage = state.personalAccessTokenError,
                            imeAction = ImeAction.Next
                        )

                        CustomTextField(
                            label = stringResource(Res.string.azure_organization),
                            value = state.organization,
                            onValueChange = vm::onOrganizationChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = stringResource(Res.string.azure_organization_placeholder),
                            isError = state.organizationError != null,
                            errorMessage = state.organizationError,
                            imeAction = ImeAction.Next
                        )

                        CustomTextField(
                            label = stringResource(Res.string.azure_project_name),
                            value = state.projectName,
                            onValueChange = vm::onProjectNameChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = stringResource(Res.string.azure_project_name_placeholder),
                            isError = state.projectNameError != null,
                            errorMessage = state.projectNameError,
                            imeAction = ImeAction.Next
                        )


                        Spacer(modifier = Modifier.height(4.dp))

                        CustomPrimaryButton(
                            text = stringResource(Res.string.save_settings),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = vm::saveSettings
                        )
                    }
                }
            }
        }
    }
}
