package dev.cjrv.azureversionator.data.network

import dev.cjrv.azureversionator.data.model.AzureDevOpsConfig
import kotlinx.serialization.Serializable

interface AzureDevOpsApi {
    /** Triggers a pipeline run and returns the created run details. */
    suspend fun runPipeline(config: AzureDevOpsConfig): Result<PipelineRunResponse>
}

@Serializable
data class PipelineRunResponse(
    val id: Int,
    val state: String,
    val result: String? = null,
    val url: String? = null
)

