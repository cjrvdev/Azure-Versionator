package dev.cjrv.azureversionator.data.model.azure

data class AzurePipeline(
    val id: String,
    val name: String,
    val folder: String? = null
)