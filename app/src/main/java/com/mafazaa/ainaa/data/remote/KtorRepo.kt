package com.mafazaa.ainaa.data.remote

import android.util.Log
import com.mafazaa.ainaa.Constants
import com.mafazaa.ainaa.model.ReportDto
import com.mafazaa.ainaa.model.VersionDto
import com.mafazaa.ainaa.model.repo.RemoteRepo
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json.Default.parseToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.io.File


class KtorRepo : RemoteRepo {

    val phoneNumberFormUrl =
        "https://docs.google.com/forms/d/1SisfmhFyuSPjafRb3UKf66QBgjO83Bcxc7DfIkddRiM/formResponse "
    val phoneNumberEntryId = "entry.1388739102" // Your Entry ID

    val reportProblemFormUrl =
        "https://docs.google.com/forms/d/e/1FAIpQLSfnspR0eAjWtYKbweOQSAOrq2OWR8dkvyEy_uj0XokrNiHnPw/formResponse"
    val reportNameEntryId = "entry.654511892"
    val reportPhoneNumberEntryId = "entry.1692603949"
    val reportEmailEntryId = "entry.742958279"
    val reportProblemEntryId = "entry.1462277143"

    val latestVersionUrl =
        "https://api.github.com/repos/mafazaa-org/Ainaa-android/releases/latest"

    /**
     * IMPORTANT:
     * we here make two assumptions:
     * 1. version code is an integer and is derived from the tag name by removing
     * the 'v' prefix. e.g. v23 -> 23
     * 2. the apk asset in the release contains the `Constants.releaseApkName`
     */
    override suspend fun getLatestVersion(): VersionDto? {
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
            val downloadAsset = assets
                .map { it.jsonObject }
                .firstOrNull { it["name"]?.jsonPrimitive?.content?.contains(Constants.releaseApkName) == true }
            val downloadUrl =
                downloadAsset?.get("browser_download_url")?.jsonPrimitive?.content.orEmpty()
            val size = downloadAsset?.get("size")?.jsonPrimitive?.longOrNull ?: 0L
            val version = tagName.removePrefix("v").toInt()
            return VersionDto(version, name, downloadUrl, body, size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun downloadFile(url: String, file: File): Boolean {
        return try {
            Log.d("KtorRepo", "Downloading file from $url to ${file.absolutePath}")
            val response: HttpResponse = client.get(url)
            if (response.status.isSuccess()) {
                val bytes = response.readBytes()
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

    val client = HttpClient(Android) {
        install(Logging) {
            level = LogLevel.BODY
        }
    }


    override fun submitPhoneNumberToGoogleForm(phoneNumber: String): Flow<NetworkResult> = flow {
        emit(NetworkResult.Loading)
        try {
            val response: HttpResponse = client.post(phoneNumberFormUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("$phoneNumberEntryId=$phoneNumber")
            }

            if (!response.status.isSuccess()) {
                emit(NetworkResult.Error("Failed: ${response.status}"))
            } else {
                emit(NetworkResult.Success)
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage))
        }
    }

    override fun submitReportToGoogleForm(reportDto: ReportDto): Flow<NetworkResult> = flow {
        emit(NetworkResult.Loading)
        try {
            val response: HttpResponse = client.post(reportProblemFormUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(Parameters.build {
                    append(reportNameEntryId, reportDto.name)
                    append(reportPhoneNumberEntryId, reportDto.phone)
                    append(reportEmailEntryId, reportDto.email)
                    append(reportProblemEntryId, reportDto.problem)
                }.formUrlEncode())
            }

            if (!response.status.isSuccess()) {
                emit(NetworkResult.Error("Failed: ${response.status}"))
            } else {
                emit(NetworkResult.Success)
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.localizedMessage))
        }

    }
}

//useful for quick testing
suspend fun main() {
    val repo = KtorRepo()
    repo.submitReportToGoogleForm(ReportDto("f", "f", "f", "f")).collect {
        println(it)
    }
}
