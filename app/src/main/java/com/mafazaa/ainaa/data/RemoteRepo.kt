package com.mafazaa.ainaa.data

import android.net.Uri
import com.mafazaa.ainaa.model.*
import kotlinx.coroutines.flow.*
import java.io.File

interface RemoteRepo {
    fun submitPhoneNumberToGoogleForm(phoneNumber: String): Flow<SubmitResult>
    fun submitReportToGoogleForm(report: Report): Flow<SubmitResult>
    suspend fun getLatestVersion(): Version?
    suspend fun downloadFile(
        url: String,
        file: File,
    ): Boolean


}