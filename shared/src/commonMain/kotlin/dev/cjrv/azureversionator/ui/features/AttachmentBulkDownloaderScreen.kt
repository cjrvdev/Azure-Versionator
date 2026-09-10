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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.attachment_bulk_downloader
import azureversionator.shared.generated.resources.download
import azureversionator.shared.generated.resources.download_attachments
import azureversionator.shared.generated.resources.error
import azureversionator.shared.generated.resources.folder
import azureversionator.shared.generated.resources.ok
import azureversionator.shared.generated.resources.select_download_path
import azureversionator.shared.generated.resources.success
import azureversionator.shared.generated.resources.workitem_id
import azureversionator.shared.generated.resources.workitem_id_help
import azureversionator.shared.generated.resources.workitem_id_placeholder
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.theme.MarginSmall
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomPrimaryButton
import dev.cjrv.azureversionator.ui.composables.CustomSecondaryCompactButton
import dev.cjrv.azureversionator.ui.composables.CustomTextFieldWithHelp
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.LinearFiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TechLabel
import dev.cjrv.azureversionator.ui.composables.TechPanel
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AttachmentBulkDownloaderScreen(onNavigateBack: () -> Unit) {
    val vm = koinViewModel<AttachmentBulkDownloaderViewModel>()
    val state by vm.state.collectAsState()

    if (state.successMessage != null) {
        AlertDialog(
            onDismissRequest = { vm.onSuccessMessageConsumed() },
            shape = RoundedCornerShape(4.dp),
            title = { TechLabel(text = stringResource(Res.string.success)) },
            text = { Text(state.successMessage.orEmpty()) },
            confirmButton = {
                TextButton(onClick = {
                    vm.onSuccessMessageConsumed()
                }) {
                    TechLabel(text = stringResource(Res.string.ok))
                }
            }
        )
    }

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
        Scaffold(
            topBar = {
                TopAppBar(
                    stringResource(Res.string.attachment_bulk_downloader),
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
                        if (state.downloadingMessage.isNullOrBlank()) {
                            InfiniteLoadingIndicator()
                        } else {
                            LinearFiniteLoadingIndicator(
                                state.downloadingMessageCurrentFileIndex!!,
                                state.downloadingMessageFileTotalAmount!!,
                                state.downloadingMessage!!
                            )
                        }
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
                                .verticalScroll(rememberScrollState())
                        ) {
                            CustomTextFieldWithHelp(
                                label = stringResource(Res.string.workitem_id),
                                value = state.workitemId,
                                helpText = stringResource(Res.string.workitem_id_help),
                                onValueChange = vm::onWorkitemIdChange,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = stringResource(Res.string.workitem_id_placeholder),
                                isError = state.workitemIdError != null,
                                errorMessage = state.workitemIdError,
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next,
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MarginSmall)
                            ) {
                                Text(
                                    state.destinationPath,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                CustomSecondaryCompactButton(
                                    stringResource(Res.string.select_download_path),
                                    leadingIcon = {
                                        Icon(
                                            imageVector = vectorResource(Res.drawable.folder),
                                            contentDescription = stringResource(Res.string.select_download_path)
                                        )
                                    },
                                    onClick = vm::onSelectDownloadPath
                                )
                            }

                            CustomPrimaryButton(
                                text = stringResource(Res.string.download_attachments),
                                onClick = vm::downloadAttachments,
                                enabled = state.isConfigurationValid,
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
}
