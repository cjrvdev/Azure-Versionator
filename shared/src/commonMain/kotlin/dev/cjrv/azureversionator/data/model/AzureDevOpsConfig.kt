package dev.cjrv.azureversionator.data.model

data class AzureDevOpsConfig(
    val organization: String = "",
    val projectName: String = "",
    val personalAccessToken: String = ""
)

