package dev.cjrv.azureversionator.data.model.azure

data class AzureDevOpsConfig(
    val organization: String = "",
    val projectName: String = "",
    val personalAccessToken: String = "",

)

data class AzureDevOpsPreferencesFilter(
    val branchFilter: List<String> = emptyList(),
    val pipelineFilter: List<String> = emptyList(),
    val repositoryFilter: List<String> = emptyList()
)

