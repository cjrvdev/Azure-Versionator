package dev.cjrv.azureversionator.data.model.app

import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsPreferencesFilter
import dev.cjrv.azureversionator.data.model.azure.AzureVariable
import kotlinx.serialization.Serializable

@Serializable
data class SharedProfile(
    val name: String,
    val teamProjectName: String = "",
    val organizationName: String = "",
    val variables: List<AzureVariable> = emptyList(),
    val filters: AzureDevOpsPreferencesFilter = AzureDevOpsPreferencesFilter()
) {
    fun toProfile(): Profile = Profile(
        name = name,
        teamProjectName = teamProjectName,
        organizationName = organizationName,
        variables = variables,
        filters = filters
    )

    companion object {
        fun fromProfile(profile: Profile): SharedProfile = SharedProfile(
            name = profile.name,
            teamProjectName = profile.teamProjectName,
            organizationName = profile.organizationName,
            variables = profile.variables,
            filters = profile.filters
        )
    }
}
