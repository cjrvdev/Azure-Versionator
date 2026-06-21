package dev.cjrv.azureversionator.data.network

import dev.cjrv.azureversionator.data.model.AzureDevOpsConfig
import dev.cjrv.azureversionator.data.model.PipelineVariables
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AzureDevOpsApiImpl(
    private val httpClient: HttpClient
) : AzureDevOpsApi {

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun runPipeline(
        config: AzureDevOpsConfig,
        variables: PipelineVariables
    ): Result<PipelineRunResponse> {
        return runCatching {
            val credentials = Base64.encode(":${config.personalAccessToken}".encodeToByteArray())
            val url = "https://dev.azure.com/${config.organization}/${config.projectName}" +
                    "/_apis/pipelines/${config.pipelineId}/runs?api-version=7.1"

            val body = buildPipelineRequestBody(variables)

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

    private fun buildPipelineRequestBody(variables: PipelineVariables): String {
        return if (variables.versionName.isNotBlank() || variables.versionCode.isNotBlank() || variables.releaseNotes.isNotBlank()) {
            """
            {
                "variables": {
                    "VersionName": {
                        "value": "${escapeJsonString(variables.versionName)}",
                        "isSecret": false
                    },
                    "VersionCode": {
                        "value": "${escapeJsonString(variables.versionCode)}",
                        "isSecret": false
                    },
                    "ReleaseNotes": {
                        "value": "${escapeJsonString(variables.releaseNotes)}",
                        "isSecret": false
                    }
                }
            }
            """.trimIndent()
        } else {
            """{"resources":{"repositories":{"self":{"refName":"refs/heads/main"}}}}"""
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
}

