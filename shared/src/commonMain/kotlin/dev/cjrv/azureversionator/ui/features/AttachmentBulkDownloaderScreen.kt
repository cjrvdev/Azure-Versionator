package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.azure_pat
import azureversionator.shared.generated.resources.azure_pat_help
import azureversionator.shared.generated.resources.azure_pat_placeholder
import azureversionator.shared.generated.resources.download
import azureversionator.shared.generated.resources.download_attachments
import azureversionator.shared.generated.resources.save
import azureversionator.shared.generated.resources.save_settings
import azureversionator.shared.generated.resources.settings
import azureversionator.shared.generated.resources.workitem_id
import azureversionator.shared.generated.resources.workitem_id_help
import azureversionator.shared.generated.resources.workitem_id_placeholder
import dev.cjrv.azureversionator.theme.CornerRadius
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomPrimaryButton
import dev.cjrv.azureversionator.ui.composables.CustomTextFieldWithHelp
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AttachmentBulkDownloaderScreen(onNavigateBack: () -> Unit) {
    val vm = koinViewModel<AttachmentBulkDownloaderViewModel>()
    val state by vm.state.collectAsState()

    Screen {
        Scaffold(
            topBar = {
                TopAppBar(
                    stringResource(Res.string.settings),
                    hasBackButton = true,
                    onBackPressed = { onNavigateBack() }
                )
            }
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
                        CustomTextFieldWithHelp(
                            label = stringResource(Res.string.workitem_id),
                            value = state.workitemId,
                            helpText = stringResource(Res.string.workitem_id_help),
                            onValueChange = vm::onWorkitemIdChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = stringResource(Res.string.workitem_id_placeholder),
                            isPassword = true,
                            isError = state.workitemIdError != null,
                            errorMessage = state.workitemIdError,
                            imeAction = ImeAction.Done,
                        )

                        CustomPrimaryButton(
                            text = stringResource(Res.string.download_attachments),
                            onClick = { vm.downloadAttachments() },
                            enabled = !state.isDownloading,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.download),
                                    contentDescription = stringResource(Res.string.download_attachments)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}