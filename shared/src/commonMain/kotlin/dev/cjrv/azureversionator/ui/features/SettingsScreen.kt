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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.settings
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

    LaunchedEffect(state.savedFeedback) {
        if (state.savedFeedback) {
            snackbarHostState.showSnackbar("Settings saved")
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
                    .background(MaterialTheme.colorScheme.background)
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
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(CornerRadius)
                            )
                            .padding(MarginMedium)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Azure DevOps",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        CustomTextField(
                            label = "Organization",
                            value = state.organization,
                            onValueChange = vm::onOrganizationChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "my-org",
                            isError = state.organizationError != null,
                            errorMessage = state.organizationError,
                            imeAction = ImeAction.Next
                        )

                        CustomTextField(
                            label = "Project name",
                            value = state.projectName,
                            onValueChange = vm::onProjectNameChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "my-project",
                            isError = state.projectNameError != null,
                            errorMessage = state.projectNameError,
                            imeAction = ImeAction.Next
                        )

                        CustomTextField(
                            label = "Personal Access Token",
                            value = state.personalAccessToken,
                            onValueChange = vm::onPersonalAccessTokenChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "••••••••",
                            isPassword = true,
                            isError = state.personalAccessTokenError != null,
                            errorMessage = state.personalAccessTokenError,
                            imeAction = ImeAction.Next
                        )

                        CustomTextField(
                            label = "Pipeline ID",
                            value = state.pipelineId,
                            onValueChange = vm::onPipelineIdChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = "42",
                            keyboardType = KeyboardType.Number,
                            isError = state.pipelineIdError != null,
                            errorMessage = state.pipelineIdError,
                            imeAction = ImeAction.Done
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        CustomPrimaryButton(
                            text = "Save settings",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = vm::saveSettings
                        )
                    }
                }
            }
        }
    }
}
