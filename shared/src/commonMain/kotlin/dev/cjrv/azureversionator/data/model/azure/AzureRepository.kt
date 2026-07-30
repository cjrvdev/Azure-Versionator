package dev.cjrv.azureversionator.data.model.azure

data class AzureRepository(
    val id: String,
    val name: String,
    val defaultBranch: String? = null
)