package dev.cjrv.azureversionator.ui.composables

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import azureversionator.shared.generated.resources.Res
import azureversionator.shared.generated.resources.chevron_back
import org.jetbrains.compose.resources.vectorResource

@Composable
fun TopAppBar(
    text: String,
    hasBackButton: Boolean = true,
    onBackPressed: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(text) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        navigationIcon = {
            if (hasBackButton)
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.chevron_back),
                        contentDescription = null
                    )
                }
        }
    )
}