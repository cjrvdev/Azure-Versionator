package dev.cjrv.azureversionator.data.model

data class AzureBranch(
    val name: String,
    val fullName: String,
    val objectId: String? = null
)