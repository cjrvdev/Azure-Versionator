package dev.cjrv.azureversionator.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PipelineVariables(
    val versionName: String = "",
    val versionCode: String = "",
    val releaseNotes: String = ""
)

