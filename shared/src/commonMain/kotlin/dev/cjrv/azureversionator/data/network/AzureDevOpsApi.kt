package dev.cjrv.azureversionator.data.network

import dev.cjrv.azureversionator.data.model.AzureDevOpsConfig
import dev.cjrv.azureversionator.data.model.PipelineVariables
import kotlinx.serialization.Serializable

interface AzureDevOpsApi {
    /**
     * Triggers a pipeline run and returns the created run details.
     *
     * @param config Azure DevOps configuration (org, project, token, pipeline ID)
     * @param variables Pipeline variables (non-secret): versionName, versionCode, releaseNotes
     */
    suspend fun runPipeline(
        config: AzureDevOpsConfig,
        variables: PipelineVariables = PipelineVariables()
    ): Result<PipelineRunResponse>
}

@Serializable
data class PipelineRunResponse(
    val id: Int,
    val state: String,
    val result: String? = null,
    val url: String? = null
)

