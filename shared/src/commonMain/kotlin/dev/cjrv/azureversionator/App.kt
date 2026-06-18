package dev.cjrv.azureversionator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import dev.cjrv.azureversionator.di.appModule
import dev.cjrv.azureversionator.navigation.AppNavigation
import dev.cjrv.azureversionator.navigation.HomeScreen
import dev.cjrv.azureversionator.navigation.Screen
import dev.cjrv.azureversionator.theme.AzureVersionatorTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(
        configuration = koinConfiguration {
            modules(appModule)
        }
    ) {
        AzureVersionatorTheme {
            val backStack = remember { mutableStateListOf<Screen>(HomeScreen) }
            AppNavigation(backStack = backStack)
        }
    }
}