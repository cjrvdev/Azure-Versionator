package dev.cjrv.azureversionator.data.settings

import com.russhwolf.settings.Settings
import dev.cjrv.azureversionator.data.model.app.Profile
import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsConfig
import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsPreferencesFilter
import kotlinx.serialization.json.Json

class AzureSettingsRepositoryImpl(
    private val settings: Settings
) : AzureSettingsRepository {

    override fun loadConfig(): AzureDevOpsConfig = AzureDevOpsConfig(
        organization = settings.getString(KEY_ORGANIZATION, ""),
        personalAccessToken = settings.getString(KEY_PAT, ""),
    )

    override fun saveConfig(config: AzureDevOpsConfig) {
        settings.putString(KEY_ORGANIZATION, config.organization)
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
        settings.putString(KEY_FILTER_BRANCH, branches)
        settings.putString(KEY_FILTER_PIPELINE, pipelines)
        settings.putString(KEY_FILTER_REPOSITORY, repositories)
    }

    override fun loadProfiles(): List<Profile> {
        val profileKeys = settings.keys.filter { it.startsWith(KEY_PROFILES) }

        // No profiles
        if (profileKeys.isEmpty()){
            val defaultProfile = Profile(
                name = "Default",
                teamProjectName = "",
                variables = emptyList()
            )
            saveProfile(defaultProfile)
            setActiveProfile(defaultProfile)
            return listOf(defaultProfile)
        }
        return profileKeys.mapNotNull { key ->
            val profileString = settings.getStringOrNull(key)
            profileString?.let { Json.decodeFromString<Profile>(it) }
        }
    }

    override fun saveProfile(profile: Profile) {
        val saveKey = "${KEY_PROFILES}_${profile.id}"
        settings.putString(saveKey, Json.encodeToString(profile))
    }

    override fun deleteProfile(profile: Profile) {
        settings.remove("${KEY_PROFILES}_${profile.id}")
    }

    override fun getProfile(id: String): Profile? {
        val profileString = settings.getStringOrNull("${KEY_PROFILES}_$id")
        return profileString?.let { Json.decodeFromString<Profile>(it) }
    }

    override fun setActiveProfile(profile: Profile) {
        settings.putString(KEY_DEFAULT_SELECTED_PROFILE_ID, profile.id)
    }

    override fun getActiveProfile(): Profile {
        val defaultProfileId = settings.getString(KEY_DEFAULT_SELECTED_PROFILE_ID, "")
        return getProfile(defaultProfileId) ?: loadProfiles().first()
    }

    private companion object {
        const val KEY_ORGANIZATION = "azure_organization"
        const val KEY_PAT = "azure_pat"
        const val KEY_FILTER_BRANCH = "branch_filter"
        const val KEY_FILTER_PIPELINE = "pipeline_filter"
        const val KEY_FILTER_REPOSITORY = "repository_filter"
        const val KEY_PROFILES = "profiles"
        const val KEY_DEFAULT_SELECTED_PROFILE_ID = "default_profile"
    }
}

