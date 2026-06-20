package dev.cjrv.azureversionator

import androidx.compose.runtime.Composable
import dev.cjrv.azureversionator.di.createKoinConfiguration
import dev.cjrv.azureversionator.navigation.Navigation
import dev.cjrv.azureversionator.theme.AzureVersionatorTheme
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(configuration = createKoinConfiguration())
    {
        AzureVersionatorTheme {
            Navigation()
        }
    }
}