package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.base64_encode_decode
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TechPanel
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Base64ProcessorScreen (onNavigateBack: () -> Unit){
    val vm = koinViewModel<Base64ProcessorViewModel>()
    val state by vm.state.collectAsState()

    Screen {
        Scaffold(
            topBar = {
                TopAppBar(
                    stringResource(Res.string.base64_encode_decode),
                    hasBackButton = true,
                    onBackPressed = { onNavigateBack() }
                )
            }
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
                    TechPanel(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MarginMedium)
                    ) {

                    }
                }
            }
        }
    }
}