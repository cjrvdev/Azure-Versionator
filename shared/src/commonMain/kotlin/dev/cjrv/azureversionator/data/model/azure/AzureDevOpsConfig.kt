package dev.cjrv.azureversionator.data.model.azure

import kotlinx.serialization.Serializable

data class AzureDevOpsConfig(
    val organization: String = "",
    val personalAccessToken: String = "",
)

@Serializable
data class AzureDevOpsPreferencesFilter(
    val branchFilter: List<String> = emptyList(),
    val pipelineFilter: List<String> = emptyList(),
    val repositoryFilter: List<String> = emptyList()
)
