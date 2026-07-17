package dev.cjrv.azureversionator.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.cjrv.azureversionator.theme.MarginMedium
import dev.cjrv.azureversionator.theme.MarginSmall


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InfiniteLoadingIndicator(loadingText: String = "") {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = MarginMedium)
    ) {
        ContainedLoadingIndicator(modifier = Modifier.size(128.dp))
        if (loadingText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(MarginSmall))
            Text(loadingText)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LinearFiniteLoadingIndicator(progress: Float, limit: Float, loadingText: String = "") {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = MarginMedium)
    ) {
        LinearWavyProgressIndicator(progress = { progress / limit }, modifier = Modifier.width(200.dp))
        if (loadingText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(MarginSmall))
            Text(loadingText)
        }
    }
}