package dev.cjrv.azureversionator.data.settings

import com.russhwolf.settings.Settings
import dev.cjrv.azureversionator.data.model.app.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json

class AzureSettingsRepositoryImpl(
    private val settings: Settings
) : AzureSettingsRepository {
    private val _profiles = MutableStateFlow<List<Profile>>(emptyList())
    override val profiles: StateFlow<List<Profile>> = _profiles

    private val _activeProfileId = MutableStateFlow<String?>(null)
    override val activeProfileId: StateFlow<String?> = _activeProfileId

    init {
        syncProfileState()
    }

    override fun loadProfiles(): List<Profile> {
        return syncProfileState()
    }

    override fun saveProfile(profile: Profile) {
        val saveKey = "${KEY_PROFILES}_${profile.id}"
        settings.putString(saveKey, Json.encodeToString(profile))
        syncProfileState()
    }

    override fun deleteProfile(profile: Profile) {
        settings.remove("${KEY_PROFILES}_${profile.id}")
        syncProfileState()
    }

    override fun getProfile(id: String): Profile? {
        val profileString = settings.getStringOrNull("${KEY_PROFILES}_$id")
        return profileString?.let { Json.decodeFromString<Profile>(it) }
    }

    override fun setActiveProfile(profile: Profile) {
        settings.putString(KEY_DEFAULT_SELECTED_PROFILE_ID, profile.id)
        syncProfileState()
    }

    override fun getActiveProfile(): Profile {
        val profiles = syncProfileState()
        val selectedProfileId = _activeProfileId.value
        return profiles.firstOrNull { it.id == selectedProfileId } ?: profiles.first()
    }

    override fun createNewProfile(name: String): Profile {
        val defaultProfile = Profile(
            name = name,
            teamProjectName = "",
            organizationName = "",
            personalAccessToken = "",
            variables = emptyList()
        )
        saveProfile(defaultProfile)
        setActiveProfile(defaultProfile)

        return defaultProfile
    }

    private fun syncProfileState(): List<Profile> {
        val profiles = readStoredProfiles().ifEmpty {
            val defaultProfile = Profile(
                name = "New profile",
                teamProjectName = "",
                organizationName = "",
                personalAccessToken = "",
                variables = emptyList()
            )
            val saveKey = "${KEY_PROFILES}_${defaultProfile.id}"
            settings.putString(saveKey, Json.encodeToString(defaultProfile))
            listOf(defaultProfile)
        }
        val selectedProfile = profiles.firstOrNull { it.id == settings.getString(KEY_DEFAULT_SELECTED_PROFILE_ID, "") }
            ?: profiles.first().also { settings.putString(KEY_DEFAULT_SELECTED_PROFILE_ID, it.id) }

        _profiles.value = profiles
        _activeProfileId.value = selectedProfile.id

        return profiles
    }

    private fun readStoredProfiles(): List<Profile> {
        val profileKeys = settings.keys.filter { it.startsWith(KEY_PROFILES) }
        return profileKeys.mapNotNull { key ->
            val profileString = settings.getStringOrNull(key)
            profileString?.let { Json.decodeFromString<Profile>(it) }
        }
    }

    private companion object {
        const val KEY_PROFILES = "profiles"
        const val KEY_DEFAULT_SELECTED_PROFILE_ID = "default_profile"
    }
}
