package dev.cjrv.azureversionator.data.model

data class AzureDevOpsConfig(
    val organization: String = "",
    val projectName: String = "",
    val personalAccessToken: String = "",

)

data class AzureDevOpsPreferencesFilter(
    val branchFilter: String = "",
    val pipelineFilter: String = "",
    val repositoryFilter: String = ""
)

