package com.mafazaa.ainaa.data

// import android.util.Log
// import com.mafazaa.ainaa.*
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.model.repo.*
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*
// import kotlinx.serialization.json.*
// import kotlinx.serialization.json.Json.Default.parseToJsonElement
// import java.io.*

class KtorRepo: RemoteRepo {

    val phoneNumberFormUrl = "https://docs.google.com/forms/d/1SisfmhFyuSPjafRb3UKf66QBgjO83Bcxc7DfIkddRiM/ ";
    val phoneNumberEntryId = "entry.1388739102" // Your Entry ID

    val reportProblemFormUrl =
        "https://docs.google.com/forms/d/e/1FAIpQLSfnspR0eAjWtYKbweOQSAOrq2OWR8dkvyEy_uj0XokrNiHnPw/formResponse"
    val reportNameEntryId = "entry.654511892";
    val reportPhoneNumberEntryId = "entry.1692603949";
    val reportEmailEntryId = "entry.742958279";
    val reportProblemEntryId = "entry.1462277143";
    
        // Initialize Ktor client
    val client = HttpClient(Android) {
        install(Logging) {
            level = LogLevel.BODY
        }
    }
    // val latestVersionUrl =
    //     "https://api.github.com/repos/mafazaa-org/Ainaa-android/releases/latest"

    // override suspend fun getLatestVersion(): Version? {
    //     return try {
    //         val client = HttpClient(Android)
    //         val response: HttpResponse =
    //             client.get(latestVersionUrl) {
    //                 header("Accept", "application/vnd.github+json")
    //                 header("X-GitHub-Api-Version", "2022-11-28")
    //             }
    //         val json = parseToJsonElement(response.bodyAsText()).jsonObject
    //         val tagName = json["tag_name"]?.jsonPrimitive?.content.orEmpty()
    //         val name = json["name"]?.jsonPrimitive?.content.orEmpty()
    //         val body = json["body"]?.jsonPrimitive?.content.orEmpty()
    //         val assets = json["assets"]?.jsonArray.orEmpty()
    //         val downloadAsset = assets
    //             .map { it.jsonObject }
    //             .firstOrNull { it["name"]?.jsonPrimitive?.content?.contains(Constants.releaseApkName) == true }
    //         val downloadUrl =
    //             downloadAsset?.get("browser_download_url")?.jsonPrimitive?.content.orEmpty()
    //         val size = downloadAsset?.get("size")?.jsonPrimitive?.longOrNull ?: 0L
    //         val version = tagName.removePrefix("v").toInt()
    //         return Version(version, name, downloadUrl, body,size)
    //     } catch (e: Exception) {
    //         e.printStackTrace()
    //         null
    //     }
    // }

    // override suspend fun downloadFile(url: String, file: File): Boolean {
    //     return try {
    //         Log.d("KtorRepo", "Downloading file from $url to ${file.absolutePath}")
    //         val response: HttpResponse = client.get(url)
    //         if (response.status.isSuccess()) {
    //             val bytes = response.readBytes()
    //             file.apply {
    //                 parentFile?.mkdirs() // Ensure parent directories exist
    //                 writeBytes(bytes) // Write the downloaded bytes to the file
    //             }
    //             true
    //         } else {
    //             false
    //         }
    //     } catch (e: Exception) {
    //         e.printStackTrace()
    //         false
    //     }
    // }

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

    override fun submitReportToGoogleForm(report: Report): Flow<NetworkResult> = flow {
        emit(NetworkResult.Loading)
        try {
            val response: HttpResponse = client.post(reportProblemFormUrl) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    "${reportNameEntryId}=${report.name}&" + // name
                            "${reportPhoneNumberEntryId}=${report.phone}&" + // phoneNumber
                            "${reportEmailEntryId}=${report.email}&" + // mail
                            "${reportProblemEntryId}=${report.problem}" // problem
                )
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

suspend fun main() {
    val repo = KtorRepo()
    repo.submitReportToGoogleForm(Report("f","f","f","f")).collect {
        println(it)
    }
}

