package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.add_task
import azureversionator.shared.generated.resources.app_name
import azureversionator.shared.generated.resources.attachment_bulk_downloader
import azureversionator.shared.generated.resources.download
import azureversionator.shared.generated.resources.new_version
import azureversionator.shared.generated.resources.settings
import dev.cjrv.azureversionator.navigation.AttachmentBulkDownloader
import dev.cjrv.azureversionator.navigation.NewVersion
import dev.cjrv.azureversionator.navigation.Route
import dev.cjrv.azureversionator.navigation.Settings
import dev.cjrv.azureversionator.theme.CornerRadius
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.theme.MarginSmall
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.CustomPrimaryButton
import dev.cjrv.azureversionator.ui.composables.CustomSecondaryButton
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(navigateToTarget: (Route) -> Unit) {
    val vm = koinViewModel<HomeViewModel>()
    val state by vm.state.collectAsState()

    Screen {
        Scaffold(topBar = {
            TopAppBar(
                stringResource(Res.string.app_name),
                hasBackButton = false
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
                                MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(CornerRadius)
                            )
                            .padding(MarginMedium)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())

                        ) {
                            CustomPrimaryButton(
                                stringResource(Res.string.new_version),
                                onClick = { navigateToTarget(NewVersion) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.add_task),
                                        contentDescription = stringResource(Res.string.new_version)
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(MarginMedium))
                            CustomPrimaryButton(
                                    stringResource(Res.string.attachment_bulk_downloader),
                            onClick = { navigateToTarget(AttachmentBulkDownloader) },
                            leadingIcon = {
                                Icon(
                                    imageVector = vectorResource(Res.drawable.download),
                                    contentDescription = stringResource(Res.string.attachment_bulk_downloader)
                                )
                            }
                            )
                            Spacer(modifier = Modifier.height(MarginMedium))
                            CustomSecondaryButton(
                                stringResource(Res.string.settings),
                                onClick = { navigateToTarget(Settings) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = vectorResource(Res.drawable.settings),
                                        contentDescription = stringResource(Res.string.settings)
                                    )
                                }
                            )
                        }
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Box(
                                Modifier
                                    .border(
                                        BorderStroke(
                                            width = 1.5.dp,
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
                                        ), RoundedCornerShape(CornerRadius)
                                    )
                                    .clickable() { vm.openAboutMe() }
                                    .padding(MarginSmall)
                            ) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                color = MaterialTheme.colorScheme.secondary.copy(
                                                    alpha = 0.85f
                                                )
                                            )
                                        )
                                        {
                                            append("Made by Cjrv.dev with ")
                                        }
                                        withStyle(style = SpanStyle(color = Color.Red))
                                        {
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

