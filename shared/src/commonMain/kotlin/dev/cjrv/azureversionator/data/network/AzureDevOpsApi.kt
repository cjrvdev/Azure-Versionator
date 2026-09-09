package dev.cjrv.azureversionator.data.network

import dev.cjrv.azureversionator.data.model.app.Profile
import dev.cjrv.azureversionator.data.model.azure.AzureBranch
import dev.cjrv.azureversionator.data.model.azure.AzurePipeline
import dev.cjrv.azureversionator.data.model.azure.AzureRepository
import dev.cjrv.azureversionator.data.model.azure.AzureWorkItemAttachment
import dev.cjrv.azureversionator.data.model.azure.PipelineRunResponse

interface AzureDevOpsApi {
    /**
     * Triggers a pipeline run and returns the created run details.
     *
     */
    suspend fun runPipeline(
        selectedProfile: Profile,
        pipelineId: String,
        branchName: String? = null
    ): Result<PipelineRunResponse>

    /**
     * Returns available pipelines for the configured Azure DevOps project.
     */
    suspend fun getPipelines(selectedProfile: Profile): Result<List<AzurePipeline>>

    /**
     * Returns available Git repositories for the configured Azure DevOps project.
     */
    suspend fun getRepositories(selectedProfile: Profile): Result<List<AzureRepository>>

    /**
     * Returns available branches (refs/heads/) for the selected repository.
     */
    suspend fun getBranches(
        repositoryId: String,
        selectedProfile: Profile
    ): Result<List<AzureBranch>>

    /**
     * Returns active attached files for a work item.
     */
    suspend fun getWorkItemAttachments(
        workItemId: String,
        selectedProfile: Profile
    ): Result<List<AzureWorkItemAttachment>>

    /**
     * Downloads an attachment binary content from its relation URL.
     */
    suspend fun downloadAttachment(
        attachmentUrl: String,
        fileName: String,
        selectedProfile: Profile
    ): Result<ByteArray>
}
