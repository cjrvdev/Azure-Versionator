package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.active_profile_label
import azureversionator.shared.generated.resources.add
import azureversionator.shared.generated.resources.add_task
import azureversionator.shared.generated.resources.app_name
import azureversionator.shared.generated.resources.attachment_bulk_downloader
import azureversionator.shared.generated.resources.auth_by_label
import azureversionator.shared.generated.resources.author_name
import azureversionator.shared.generated.resources.clone_profile
import azureversionator.shared.generated.resources.copy
import azureversionator.shared.generated.resources.download
import azureversionator.shared.generated.resources.edit_profile
import azureversionator.shared.generated.resources.extract_workitem_attachments_subtitle
import azureversionator.shared.generated.resources.new_build
import azureversionator.shared.generated.resources.new_profile
import azureversionator.shared.generated.resources.ok
import azureversionator.shared.generated.resources.profile_name
import azureversionator.shared.generated.resources.profile_name_placeholder
import azureversionator.shared.generated.resources.return_text
import azureversionator.shared.generated.resources.settings
import azureversionator.shared.generated.resources.trigger_pipeline_run_subtitle
import dev.cjrv.azureversionator.navigation.AttachmentBulkDownloader
import dev.cjrv.azureversionator.navigation.EditProfile
import dev.cjrv.azureversionator.navigation.NewVersion
import dev.cjrv.azureversionator.navigation.Route
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.theme.MarginSmall
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomDropdownField
import dev.cjrv.azureversionator.ui.composables.CustomTextField
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TechCard
import dev.cjrv.azureversionator.ui.composables.TechIcon
import dev.cjrv.azureversionator.ui.composables.TechLabel
import dev.cjrv.azureversionator.ui.composables.TechPanel
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(navigateToTarget: (Route) -> Unit) {
    val vm = koinViewModel<HomeViewModel>()
    val state by vm.state.collectAsState()
    var showNewProfileDialog by remember { mutableStateOf(false) }
    var newProfileName by remember { mutableStateOf("") }

    Screen {
        Scaffold(topBar = {
            TopAppBar(
                text = stringResource(Res.string.app_name),
                hasBackButton = false
            )
        }) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                if (showNewProfileDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showNewProfileDialog = false
                            newProfileName = ""
                        },
                        shape = MaterialTheme.shapes.extraSmall,
                        title = { TechLabel(text = stringResource(Res.string.new_profile)) },
                        text = {
                            CustomTextField(
                                label = stringResource(Res.string.profile_name),
                                value = newProfileName,
                                onValueChange = { newProfileName = it },
                                placeholder = stringResource(Res.string.profile_name_placeholder),
                                imeAction = ImeAction.Done
                            )
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showNewProfileDialog = false
                                newProfileName = ""
                            }) {
                                TechLabel(
                                    text = stringResource(Res.string.return_text),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    vm.createNewProfile(newProfileName.trim())
                                    showNewProfileDialog = false
                                    newProfileName = ""
                                },
                                enabled = newProfileName.trim().isNotEmpty()
                            ) {
                                TechLabel(text = stringResource(Res.string.ok))
                            }
                        }
                    )
                }
                if (state.isLoading) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        InfiniteLoadingIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MarginMedium)
                    ) {
                        TechPanel(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(MarginMedium)) {
                                // Profile Command Module
                                TechCard(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(MarginMedium)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TechLabel(text = stringResource(Res.string.active_profile_label))
                                            Row(horizontalArrangement = Arrangement.spacedBy(MarginSmall)) {
                                                TechActionButton(
                                                    icon = Res.drawable.copy,
                                                    contentDescription = stringResource(Res.string.clone_profile),
                                                    onClick = { vm.onCloneProfileClicked() }
                                                )
                                                TechActionButton(
                                                    icon = Res.drawable.settings,
                                                    contentDescription = stringResource(Res.string.edit_profile),
                                                    onClick = { navigateToTarget(EditProfile) }
                                                )
                                                TechActionButton(
                                                    icon = Res.drawable.add,
                                                    contentDescription = stringResource(Res.string.new_profile),
                                                    onClick = { showNewProfileDialog = true },
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                )
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(MarginSmall))
                                        
                                        CustomDropdownField(
                                            label = "",
                                            options = state.profiles.map { it.id },
                                            selectedItem = state.selectedProfileId ?: "",
                                            onOptionSelected = { selectedId ->
                                                vm.onSelectedProfileChanged(selectedId)
                                            }, optionLabel = { profileId ->
                                                state.profiles.find { it.id == profileId }?.name
                                                    ?: ""
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(MarginMedium))

                                // Operations Grid
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(MarginMedium)
                                ) {
                                    OperationCard(
                                        title = stringResource(Res.string.new_build),
                                        subtitle = stringResource(Res.string.trigger_pipeline_run_subtitle),
                                        icon = Res.drawable.add_task,
                                        onClick = { navigateToTarget(NewVersion) }
                                    )

                                    OperationCard(
                                        title = stringResource(Res.string.attachment_bulk_downloader),
                                        subtitle = stringResource(Res.string.extract_workitem_attachments_subtitle),
                                        icon = Res.drawable.download,
                                        onClick = { navigateToTarget(AttachmentBulkDownloader) }
                                    )
                                }

                                // Terminal Footer
                                Box(
                                    Modifier.fillMaxWidth().padding(top = MarginMedium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CreatedByLabel(openAboutMe = vm::openAboutMe)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TechActionButton(
    icon: DrawableResource,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(34.dp),
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = vectorResource(icon),
                contentDescription = contentDescription,
                modifier = Modifier.size(18.dp),
                tint = tint
            )
        }
    }
}

@Composable
private fun CreatedByLabel(
    openAboutMe: () -> Unit,
) {
    val authLabel = stringResource(Res.string.auth_by_label)
    val author = stringResource(Res.string.author_name)
    Text(
        modifier = Modifier.clickable { openAboutMe() },
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))) {
                append(authLabel)
            }
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(author)
            }
            withStyle(SpanStyle(color = Color.Red.copy(alpha = 0.7f))) {
                append("♥")
            }
        },
        style = MaterialTheme.typography.labelSmall,
        letterSpacing = 1.sp
    )
}

@Composable
private fun OperationCard(
    title: String,
    subtitle: String,
    icon: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TechCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TechIcon(icon = icon)
            Spacer(modifier = Modifier.width(MarginMedium))
            Column {
                TechLabel(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
