package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.add
import azureversionator.shared.generated.resources.add_task
import azureversionator.shared.generated.resources.app_name
import azureversionator.shared.generated.resources.attachment_bulk_downloader
import azureversionator.shared.generated.resources.download
import azureversionator.shared.generated.resources.edit
import azureversionator.shared.generated.resources.edit_profile
import azureversionator.shared.generated.resources.new_profile
import azureversionator.shared.generated.resources.new_version
import azureversionator.shared.generated.resources.ok
import azureversionator.shared.generated.resources.profile_name
import azureversionator.shared.generated.resources.profile_name_placeholder
import azureversionator.shared.generated.resources.return_text
import azureversionator.shared.generated.resources.selected_profile
import dev.cjrv.azureversionator.navigation.AttachmentBulkDownloader
import dev.cjrv.azureversionator.navigation.EditProfile
import dev.cjrv.azureversionator.navigation.NewVersion
import dev.cjrv.azureversionator.navigation.Route
import dev.cjrv.azureversionator.theme.CornerRadius
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.theme.MarginSmall
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomDropdownField
import dev.cjrv.azureversionator.ui.composables.CustomTextField
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
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
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(innerPadding)
            ) {
                if (showNewProfileDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showNewProfileDialog = false
                            newProfileName = ""
                        },
                        title = { Text(stringResource(Res.string.new_profile)) },
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
                                Text(stringResource(Res.string.return_text))
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
                                Text(stringResource(Res.string.ok))
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
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(CornerRadius)
                            )
                            .padding(MarginMedium)
                    ) {
                        // Profile Switcher Header
                        Text(
                            text = stringResource(Res.string.selected_profile),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                CustomDropdownField(
                                    label = "",
                                    options = state.profiles.map { it.id },
                                    selectedItem = state.selectedProfileId ?: "",
                                    onOptionSelected = { selectedId ->
                                        vm.onSelectedProfileChanged(selectedId)
                                    }, optionLabel = { profileId ->
                                        state.profiles.find { it.id == profileId }?.name ?: ""
                                    },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(MarginSmall))
                            Row(modifier = Modifier.padding(top = 16.dp)) {
                                IconButton(onClick = { navigateToTarget(EditProfile) }) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.edit),
                                        contentDescription = stringResource(Res.string.edit_profile),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                IconButton(onClick = { showNewProfileDialog = true }) {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.add),
                                        contentDescription = stringResource(Res.string.new_profile),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(MarginMedium))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(MarginMedium))

                        // Main Actions
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(MarginMedium)
                        ) {
                            FeatureCard(
                                title = stringResource(Res.string.new_version),
                                icon = Res.drawable.add_task,
                                onClick = { navigateToTarget(NewVersion) }
                            )

                            FeatureCard(
                                title = stringResource(Res.string.attachment_bulk_downloader),
                                icon = Res.drawable.download,
                                onClick = { navigateToTarget(AttachmentBulkDownloader) }
                            )
                        }

                        // Footer
                        Box(
                            Modifier.fillMaxWidth().padding(top = MarginMedium),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Surface(
                                shape = RoundedCornerShape(CornerRadius),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                ),
                                color = Color.Transparent,
                                onClick = { vm.openAboutMe() }
                            ) {
                                Text(
                                    modifier = Modifier.padding(horizontal = MarginSmall, vertical = 4.dp),
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                            )
                                        ) {
                                            append("Made by Cjrv.dev with ")
                                        }
                                        withStyle(style = SpanStyle(color = Color.Red)) {
                                            append("♥")
                                        }
                                    },
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    icon: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = vectorResource(icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(MarginMedium))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
