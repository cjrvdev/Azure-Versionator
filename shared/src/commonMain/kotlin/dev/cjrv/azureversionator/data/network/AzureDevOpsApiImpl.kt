package dev.cjrv.azureversionator.data.network

import dev.cjrv.azureversionator.data.model.app.Profile
import dev.cjrv.azureversionator.data.model.azure.AzureBranch
import dev.cjrv.azureversionator.data.model.azure.AzurePipeline
import dev.cjrv.azureversionator.data.model.azure.AzureRepository
import dev.cjrv.azureversionator.data.model.azure.AzureVariable
import dev.cjrv.azureversionator.data.model.azure.AzureWorkItemAttachment
import dev.cjrv.azureversionator.data.model.azure.PipelineRunResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AzureDevOpsApiImpl(
    private val httpClient: HttpClient
) : AzureDevOpsApi {

    private val apiVersion = "7.1"

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun runPipeline(
        selectedProfile: Profile,
        pipelineId: String,
        branchName: String?
    ): Result<PipelineRunResponse> {
        return runCatching {
            val credentials = basicCredentials(selectedProfile)
            val url = "${projectApiBaseUrl(selectedProfile)}/_apis/pipelines/$pipelineId/runs?api-version=$apiVersion"

            val body = buildRunPipelineRequestBody(selectedProfile.variables, branchName)

            val response = httpClient.post(url) {
                header("Authorization", "Basic $credentials")
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            if (!response.status.isSuccess()) {
                error("Pipeline run failed with status ${response.status.value}")
            }

            response.body<PipelineRunResponse>()
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun getPipelines(selectedProfile: Profile): Result<List<AzurePipeline>> {
        return runCatching {
            val response = httpClient.get("${projectApiBaseUrl(selectedProfile)}/_apis/pipelines?api-version=$apiVersion") {
                header("Authorization", "Basic ${basicCredentials(selectedProfile)}")
            }

            if (!response.status.isSuccess()) {
                error("Pipelines request failed with status ${response.status.value}")
            }

            response.body<PipelinesResponse>().value.map { pipeline ->
                AzurePipeline(
                    id = pipeline.id.toString(),
                    name = pipeline.name,
                    folder = pipeline.folder
                )
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun getRepositories(selectedProfile: Profile): Result<List<AzureRepository>> {
        return runCatching {
            val response = httpClient.get("${projectApiBaseUrl(selectedProfile)}/_apis/git/repositories?api-version=$apiVersion") {
                header("Authorization", "Basic ${basicCredentials(selectedProfile)}")
            }

            if (!response.status.isSuccess()) {
                error("Repositories request failed with status ${response.status.value}")
            }

            response.body<RepositoriesResponse>().value.map { repository ->
                AzureRepository(
                    id = repository.id,
                    name = repository.name,
                    defaultBranch = repository.defaultBranch
                )
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun getBranches(
        repositoryId: String,
        selectedProfile: Profile
    ): Result<List<AzureBranch>> {
        return runCatching {
            val response = httpClient.get(
                "${projectApiBaseUrl(selectedProfile)}/_apis/git/repositories/$repositoryId/refs?filter=heads/&api-version=$apiVersion"
            ) {
                header("Authorization", "Basic ${basicCredentials(selectedProfile)}")
            }

            if (!response.status.isSuccess()) {
                error("Branches request failed with status ${response.status.value}")
            }

            response.body<BranchesResponse>().value.map { branch ->
                AzureBranch(
                    name = branch.name.removePrefix("refs/heads/"),
                    fullName = branch.name,
                    objectId = branch.objectId
                )
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun getWorkItemAttachments(
        workItemId: String,
        selectedProfile: Profile
    ): Result<List<AzureWorkItemAttachment>> {
        return runCatching {
            val response = httpClient.get(
                "${projectApiBaseUrl(selectedProfile)}/_apis/wit/workitems/$workItemId?\$expand=relations&api-version=$apiVersion"
            ) {
                header("Authorization", "Basic ${basicCredentials(selectedProfile)}")
            }

            if (!response.status.isSuccess()) {
                error("Work item request failed with status ${response.status.value}")
            }

            response.body<WorkItemResponse>().relations
                .asSequence()
                .filter { relation -> relation.rel.equals("AttachedFile", ignoreCase = true) }
                .filter { relation -> relation.attributes?.isDeleted != true }
                .map { relation ->
                    AzureWorkItemAttachment(
                        fileName = relation.attributes?.name
                            ?.takeIf { it.isNotBlank() }
                            ?: fallbackFileNameFromUrl(relation.url),
                        url = relation.url
                    )
                }
                .toList()
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun downloadAttachment(
        attachmentUrl: String,
        fileName: String,
        selectedProfile: Profile
    ): Result<ByteArray> {
        return runCatching {
            val downloadUrl = URLBuilder(attachmentUrl).apply {
                parameters.append("download", "true")
                parameters.append("fileName", fileName)
                parameters.append("api-version", apiVersion)
            }.buildString()

            val response = httpClient.get(downloadUrl) {
                header("Authorization", "Basic ${basicCredentials(selectedProfile)}")
            }

            if (!response.status.isSuccess()) {
                error("Attachment download failed with status ${response.status.value}")
            }

            response.body<ByteArray>()
        }
    }

    private fun buildRunPipelineRequestBody(variables: List<AzureVariable>, branchName: String?): String {
        val refName = if (!branchName.isNullOrBlank()) branchName else "refs/heads/main"

        return if (variables.isNotEmpty()) {
            """
            {
                "resources": {
                    "repositories": {
                        "self": {
                            "refName": "${escapeJsonString(refName)}"
                        }
                    }
                },
                "variables": {
                    ${buildVariableList(variables)}
                }
            }
            """.trimIndent()
        } else {
            """{"resources":{"repositories":{"self":{"refName":"${escapeJsonString(refName)}"}}}}"""
        }
    }

    private fun escapeJsonString(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun buildVariableList(variables: List<AzureVariable>): String {
        return variables.joinToString(",") { variable ->
            """
            "${variable.name}": {
                "value": "${escapeJsonString(variable.value)}",
                "isSecret": ${variable.isSecret}
            }
            """.trimIndent()
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun basicCredentials(selectedProfile: Profile): String {
        return Base64.encode(":${selectedProfile.personalAccessToken}".encodeToByteArray())
    }

    private fun projectApiBaseUrl(selectedProfile: Profile): String {
        return "https://dev.azure.com/${selectedProfile.organizationName}/${selectedProfile.teamProjectName}"
    }

    private fun fallbackFileNameFromUrl(url: String): String {
        return url.substringAfterLast('/').substringBefore('?').ifBlank { "attachment.bin" }
    }
}

@Serializable
private data class RepositoriesResponse(
    val value: List<RepositoryDto> = emptyList()
)

@Serializable
private data class RepositoryDto(
    val id: String,
    val name: String,
    @SerialName("defaultBranch") val defaultBranch: String? = null
)

@Serializable
private data class BranchesResponse(
    val value: List<BranchDto> = emptyList()
)

@Serializable
private data class BranchDto(
    val name: String,
    @SerialName("objectId") val objectId: String? = null
)

@Serializable
private data class PipelinesResponse(
    val value: List<PipelineDto> = emptyList()
)

@Serializable
private data class PipelineDto(
    val id: Int,
    val name: String,
    val folder: String? = null
)

@Serializable
private data class WorkItemResponse(
    val relations: List<WorkItemRelationDto> = emptyList()
)

@Serializable
private data class WorkItemRelationDto(
    val rel: String,
    val url: String,
    val attributes: WorkItemRelationAttributesDto? = null
)

@Serializable
private data class WorkItemRelationAttributesDto(
    val name: String? = null,
    @SerialName("isDeleted") val isDeleted: Boolean? = null
)
