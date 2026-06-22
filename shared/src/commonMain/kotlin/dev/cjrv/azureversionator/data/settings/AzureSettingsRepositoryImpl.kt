package dev.cjrv.azureversionator.data.settings

import com.russhwolf.settings.Settings
import dev.cjrv.azureversionator.data.model.AzureDevOpsConfig

class AzureSettingsRepositoryImpl(
    private val settings: Settings
) : AzureSettingsRepository {

    override fun loadConfig(): AzureDevOpsConfig = AzureDevOpsConfig(
        organization = settings.getString(KEY_ORGANIZATION, ""),
        projectName = settings.getString(KEY_PROJECT_NAME, ""),
        personalAccessToken = settings.getString(KEY_PAT, "")
    )

    override fun saveConfig(config: AzureDevOpsConfig) {
        settings.putString(KEY_ORGANIZATION, config.organization)
        settings.putString(KEY_PROJECT_NAME, config.projectName)
        settings.putString(KEY_PAT, config.personalAccessToken)
    }

    private companion object {
        const val KEY_ORGANIZATION = "azure_organization"
        const val KEY_PROJECT_NAME = "azure_project_name"
        const val KEY_PAT = "azure_pat"
    }
}

