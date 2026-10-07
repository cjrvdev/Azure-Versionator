package dev.cjrv.azureversionator.data.settings

import kotlinx.coroutines.flow.StateFlow

data class GeneralSettings(val darkTheme: Boolean)

interface AppSettingsRepository {
    val settings: StateFlow<GeneralSettings?>

    fun initialize(defaultDarkTheme: Boolean)
    fun toggleTheme()
}
