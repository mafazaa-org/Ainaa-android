package com.mafazaa.ainaa.data.repository_impl

import com.mafazaa.ainaa.core.Constants
import com.mafazaa.ainaa.data.repository_impl.data_source.remote_data_source.RemoteRepo
import com.mafazaa.ainaa.data.repository_impl.view_models.state.SubmitResult
import com.mafazaa.ainaa.domain.model.Report
import com.mafazaa.ainaa.domain.model.Version
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.statement.readRawBytes
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import java.io.*

class KtorRepo: RemoteRepo {

    val googleFormUrl =
        "https://docs.google.com/forms/d/e/1FAIpQLSdLizpsP03ZJ1epl1DfW41XZsH_Ul83HU_ZWEidOla_En-s5A/formResponse"
    val phoneNumberEntryId = "entry.1388739102" // Your Entry ID
    // Initialize Ktor client
    val client = HttpClient(Android) {
        install(Logging) {
            level = LogLevel.BODY
        }
    }
    val latestVersionUrl =
        "https://api.github.com/repos/mafazaa-org/Ainaa-android/releases/latest"

    override suspend fun getLatestVersion(): Version? {
        return try {
            val client = HttpClient(Android)
            val response: HttpResponse =
                client.get(latestVersionUrl) {
                    header("Accept", "application/vnd.github+json")
                    header("X-GitHub-Api-Version", "2022-11-28")
                }
            val json = parseToJsonElement(response.bodyAsText()).jsonObject
            val tagName = json["tag_name"]?.jsonPrimitive?.content.orEmpty()
            val name = json["name"]?.jsonPrimitive?.content.orEmpty()
            val body = json["body"]?.jsonPrimitive?.content.orEmpty()
            val assets = json["assets"]?.jsonArray.orEmpty()
            val downloadUrl = assets
                .map { it.jsonObject }
                .firstOrNull { it["name"]?.jsonPrimitive?.content == Constants.releaseName }
                ?.get("browser_download_url")?.jsonPrimitive?.content.orEmpty()
            val version = tagName.removePrefix("v").toInt()
            return Version(version, name, downloadUrl, body)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun downloadFile(url: String, file: File): Boolean {
        return try {
            val response: HttpResponse = client.get(url)
            if (response.status.isSuccess()) {
                val bytes = response.readRawBytes()
                file.apply {
                    parentFile?.mkdirs() // Ensure parent directories exist
                    writeBytes(bytes) // Write the downloaded bytes to the file
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun submitPhoneNumberToGoogleForm(phoneNumber: String): Flow<SubmitResult> = flow {
        emit(SubmitResult.Loading)
        try {
            val response: HttpResponse = client.post(googleFormUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("$phoneNumberEntryId=$phoneNumber")
            }

            if (!response.status.isSuccess()) {
                emit(SubmitResult.Error("Failed: ${response.status}"))
            } else {
                emit(SubmitResult.Success)
            }
        } catch (e: Exception) {
            emit(SubmitResult.Error(e.localizedMessage))
        }
    }

    override fun submitReportToGoogleForm(report: Report): Flow<SubmitResult> = flow {
        emit(SubmitResult.Loading)
        try {
            val response: HttpResponse = client.post(googleFormUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    "entry.654511892=${report.name}&" + // name
                            "entry.1692603949=${report.phone}&" + // phoneNumber
                            "entry.742958279=${report.email}&" + // mail
                            "entry.1462277143=${report.problem}" // problem
                )
            }

            if (!response.status.isSuccess()) {
                emit(SubmitResult.Error("Failed: ${response.status}"))
            } else {
                emit(SubmitResult.Success)
            }
        } catch (e: Exception) {
            emit(SubmitResult.Error(e.localizedMessage))
        }

    }
}

suspend fun main() {
    val repo = KtorRepo()
    print(repo.getLatestVersion())
}

