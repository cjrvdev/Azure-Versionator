package dev.cjrv.azureversionator.data.settings

import com.russhwolf.settings.Settings
import dev.cjrv.azureversionator.data.model.AzureDevOpsConfig
import dev.cjrv.azureversionator.data.model.AzureDevOpsPreferencesFilter

class AzureSettingsRepositoryImpl(
    private val settings: Settings
) : AzureSettingsRepository {

    override fun loadConfig(): AzureDevOpsConfig = AzureDevOpsConfig(
        organization = settings.getString(KEY_ORGANIZATION, ""),
        projectName = settings.getString(KEY_PROJECT_NAME, ""),
        personalAccessToken = settings.getString(KEY_PAT, ""),
    )

    override fun saveConfig(config: AzureDevOpsConfig) {
        settings.putString(KEY_ORGANIZATION, config.organization)
        settings.putString(KEY_PROJECT_NAME, config.projectName)
        settings.putString(KEY_PAT, config.personalAccessToken)
    }

    override fun loadFilters(): AzureDevOpsPreferencesFilter =
        AzureDevOpsPreferencesFilter(
            branchFilter = settings.getString(KEY_FILTER_BRANCH, "").split(';'),
            pipelineFilter = settings.getString(KEY_FILTER_PIPELINE, "").split(';'),
            repositoryFilter = settings.getString(KEY_FILTER_REPOSITORY, "").split(';'),
        )

    override fun saveFilters(
        branches: String,
        pipelines: String,
        repositories: String
    ) {
        /*settings.putString(KEY_FILTER_BRANCH, branches.joinToString(separator = ";") { it })
        settings.putString(KEY_FILTER_PIPELINE, pipelines.joinToString(separator = ";") { it })
        settings.putString(KEY_FILTER_REPOSITORY, repositories.joinToString(separator = ";") { it })*/

        settings.putString(KEY_FILTER_BRANCH, branches)
        settings.putString(KEY_FILTER_PIPELINE, pipelines)
        settings.putString(KEY_FILTER_REPOSITORY, repositories)
    }

    private companion object {
        const val KEY_ORGANIZATION = "azure_organization"
        const val KEY_PROJECT_NAME = "azure_project_name"
        const val KEY_PAT = "azure_pat"
        const val KEY_FILTER_BRANCH = "branch_filter"
        const val KEY_FILTER_PIPELINE = "pipeline_filter"
        const val KEY_FILTER_REPOSITORY = "repository_filter"
    }
}

