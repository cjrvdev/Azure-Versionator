package dev.cjrv.azureversionator.data.settings

import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsConfig
import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsPreferencesFilter

interface AzureSettingsRepository {
    fun loadConfig(): AzureDevOpsConfig
    fun saveConfig(config: AzureDevOpsConfig)
    fun loadFilters() : AzureDevOpsPreferencesFilter
    fun saveFilters(branches : String, pipelines : String, repositories : String)
}

