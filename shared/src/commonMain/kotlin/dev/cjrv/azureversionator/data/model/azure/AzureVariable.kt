package dev.cjrv.azureversionator.data.model.azure

import kotlinx.serialization.Serializable

@Serializable
data class AzureVariable(
    val name: String,
    val value: String,
    val isSecret: Boolean = false
)
