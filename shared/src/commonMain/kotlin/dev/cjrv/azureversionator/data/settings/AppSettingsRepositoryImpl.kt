package dev.cjrv.azureversionator.data.settings

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppSettingsRepositoryImpl(
    private val storage: Settings
) : AppSettingsRepository {
    private val _settings = MutableStateFlow(
        storage.getBooleanOrNull(KEY_DARK_THEME)?.let { GeneralSettings(darkTheme = it) }
    )
    override val settings = _settings.asStateFlow()

    override fun initialize(defaultDarkTheme: Boolean) {
        if (_settings.value == null) {
            save(GeneralSettings(darkTheme = defaultDarkTheme))
        }
    }

    override fun toggleTheme() {
        val current = checkNotNull(_settings.value) { "General settings have not been initialized" }
        save(current.copy(darkTheme = !current.darkTheme))
    }

    private fun save(settings: GeneralSettings) {
        storage.putBoolean(KEY_DARK_THEME, settings.darkTheme)
        _settings.value = settings
    }

    private companion object {
        const val KEY_DARK_THEME = "general_dark_theme"
    }
}
