package dev.cjrv.azureversionator.data.model

data class AzureRepository(
    val id: String,
    val name: String,
    val defaultBranch: String? = null
)