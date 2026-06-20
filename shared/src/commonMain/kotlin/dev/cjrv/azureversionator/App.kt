package dev.cjrv.azureversionator

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.cjrv.azureversionator.di.navigationModule
import dev.cjrv.azureversionator.di.platformModule
import dev.cjrv.azureversionator.navigation.Navigation
import dev.cjrv.azureversionator.theme.AzureVersionatorTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
    KoinApplication(
        configuration = koinConfiguration {
            modules(
                navigationModule,
                platformModule(),
            )
        }
    ) {
        AzureVersionatorTheme {
            Navigation()
        }
    }
}