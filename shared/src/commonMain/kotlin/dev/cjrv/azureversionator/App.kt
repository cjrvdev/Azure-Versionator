package dev.cjrv.azureversionator

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.cjrv.azureversionator.data.settings.AppSettingsRepository
import dev.cjrv.azureversionator.di.createKoinConfiguration
import dev.cjrv.azureversionator.navigation.Navigation
import dev.cjrv.azureversionator.theme.AzureVersionatorTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
fun App() {
    KoinApplication(configuration = createKoinConfiguration())
    {
        val settingsRepository = koinInject<AppSettingsRepository>()
        val settings by settingsRepository.settings.collectAsState()
        val darkTheme = settings?.darkTheme ?: isSystemInDarkTheme()

        if (settings == null) {
            LaunchedEffect(settingsRepository) {
                settingsRepository.initialize(darkTheme)
            }
        }

        AzureVersionatorTheme(darkTheme = darkTheme) {
            Navigation()
        }
    }
}