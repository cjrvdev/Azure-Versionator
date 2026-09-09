package dev.cjrv.azureversionator.data.model.app

import dev.cjrv.azureversionator.data.model.azure.AzureDevOpsPreferencesFilter
import dev.cjrv.azureversionator.data.model.azure.AzureVariable
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class Profile(
    val id: String = Uuid.random().toString(),
    val name: String = "",
    val teamProjectName : String = "",
    val organizationName : String = "",
    val personalAccessToken : String = "",
    val variables : List<AzureVariable> = emptyList(),
    val filters: AzureDevOpsPreferencesFilter = AzureDevOpsPreferencesFilter()
)
