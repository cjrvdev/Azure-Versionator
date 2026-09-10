package dev.cjrv.azureversionator.data.settings

import dev.cjrv.azureversionator.data.model.app.Profile
import kotlinx.coroutines.flow.StateFlow

interface AzureSettingsRepository {
    val profiles: StateFlow<List<Profile>>
    val activeProfileId: StateFlow<String?>

    fun loadProfiles() : List<Profile>
    fun saveProfile(profile: Profile)
    fun deleteProfile(profile: Profile)
    fun getProfile(id: String): Profile?
    fun setActiveProfile(profile: Profile)
    fun getActiveProfile(): Profile
    fun createNewProfile(name: String) : Profile
    fun cloneProfile(profile: Profile) : Profile
}
