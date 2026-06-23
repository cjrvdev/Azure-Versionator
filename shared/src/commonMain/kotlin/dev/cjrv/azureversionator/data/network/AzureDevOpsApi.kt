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
        variables: PipelineVariables = PipelineVariables(),
        pipelineId: String,
        branchName: String? = null
    ): Result<PipelineRunResponse>

    /**
     * Returns available pipelines for the configured Azure DevOps project.
     */
    suspend fun getPipelines(config: AzureDevOpsConfig): Result<List<AzurePipeline>>

    /**
     * Returns available Git repositories for the configured Azure DevOps project.
     */
    suspend fun getRepositories(config: AzureDevOpsConfig): Result<List<AzureRepository>>

    /**
     * Returns available branches (refs/heads/) for the selected repository.
     */
    suspend fun getBranches(
        config: AzureDevOpsConfig,
        repositoryId: String
    ): Result<List<AzureBranch>>
}

data class AzureRepository(
    val id: String,
    val name: String,
    val defaultBranch: String? = null
)

data class AzureBranch(
    val name: String,
    val fullName: String,
    val objectId: String? = null
)

data class AzurePipeline(
    val id: String,
    val name: String,
    val folder: String? = null
)

@Serializable
data class PipelineRunResponse(
    val id: Int,
    val state: String,
    val result: String? = null,
    val url: String? = null
)

