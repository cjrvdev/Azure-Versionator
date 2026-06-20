package dev.cjrv.azureversionator.ui.features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.app_name
import dev.cjrv.azureversionator.ui.Screen
import dev.cjrv.azureversionator.ui.composables.InfiniteLoadingIndicator
import dev.cjrv.azureversionator.ui.composables.TopAppBar
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen() {
    Screen {
        Scaffold(topBar = { TopAppBar(stringResource(Res.string.app_name), hasBackButton = false) }) { innerPadding ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                InfiniteLoadingIndicator()
            }
        }
    }
}

