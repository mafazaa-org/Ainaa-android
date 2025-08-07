package com.mafazaa.ainaa.data

import android.util.Log
import com.mafazaa.ainaa.model.*
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.*

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
    // Example usage
    val repo = KtorRepo()
    repo.submitPhoneNumberToGoogleForm("01234567890").collect {
        print(it.toString())

    }

    // Call the function with a sample phone number
    // repo.submitPhoneNumberToGoogleForm("01234567890")
}
