package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.azure_organization
import azureversionator.shared.generated.resources.azure_organization_placeholder
import azureversionator.shared.generated.resources.new_version
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
                    .background(MaterialTheme.colorScheme.background)
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
                                MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(CornerRadius)
                            )
                            .padding(MarginMedium)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row {
                            CustomTextField(
                                label = stringResource(Res.string.release_notes_version_name),
                                value = state.versionName,
                                onValueChange = vm::onVersionNameChange,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(end = MarginMedium),
                                placeholder = stringResource(Res.string.release_notes_version_name_placeholder),
                                isError = state.versionNameError != null,
                                errorMessage = state.versionNameError,
                                imeAction = ImeAction.Next
                            )
                            CustomTextField(
                                    label = stringResource(Res.string.release_notes_build_number),
                            value = state.buildNumber,
                            onValueChange = vm::onBuildNumberChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(end = MarginMedium),
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
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}