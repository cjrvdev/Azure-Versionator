package dev.cjrv.azureversionator.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PipelineRunResponse(
    val id: Int,
    val state: String,
    val result: String? = null,
    val url: String? = null
)