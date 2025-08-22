package com.mafazaa.ainaa.model.repo

import com.mafazaa.ainaa.data.NetworkResult
import com.mafazaa.ainaa.model.Report
import com.mafazaa.ainaa.model.Version
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RemoteRepo {
    fun submitPhoneNumberToGoogleForm(phoneNumber: String): Flow<NetworkResult>
    fun submitReportToGoogleForm(report: Report): Flow<NetworkResult>
    suspend fun getLatestVersion(): Version?
    suspend fun downloadFile(
        url: String,
        file: File,
    ): Boolean


}