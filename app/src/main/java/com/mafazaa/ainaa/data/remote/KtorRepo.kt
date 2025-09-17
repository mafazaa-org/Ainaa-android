package com.mafazaa.ainaa.data.remote

// import android.util.Log
// import com.mafazaa.ainaa.*
import com.mafazaa.ainaa.model.Report
import com.mafazaa.ainaa.model.repo.RemoteRepo
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// import kotlinx.serialization.json.*
// import kotlinx.serialization.json.Json.Default.parseToJsonElement
// import java.io.*

class KtorRepo: RemoteRepo {

    val phoneNumberFormUrl = "https://docs.google.com/forms/d/1SisfmhFyuSPjafRb3UKf66QBgjO83Bcxc7DfIkddRiM/formResponse ";
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
                setBody(Parameters.build {
                    append(reportNameEntryId, report.name)
                    append(reportPhoneNumberEntryId, report.phone)
                    append(reportEmailEntryId, report.email)
                    append(reportProblemEntryId, report.problem)
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

suspend fun main() {
    val repo = KtorRepo()
    repo.submitReportToGoogleForm(Report("f","f","f","f")).collect {
        println(it)
    }
}
