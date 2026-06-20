package dev.cjrv.azureversionator.data.settings

import dev.cjrv.azureversionator.data.model.AzureDevOpsConfig

interface AzureSettingsRepository {
    fun loadConfig(): AzureDevOpsConfig
    fun saveConfig(config: AzureDevOpsConfig)
}

