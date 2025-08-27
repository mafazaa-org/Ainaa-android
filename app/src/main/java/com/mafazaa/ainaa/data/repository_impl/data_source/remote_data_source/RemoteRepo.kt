package com.mafazaa.ainaa.data.repository_impl.data_source.remote_data_source

import com.mafazaa.ainaa.data.repository_impl.view_models.state.SubmitResult
import com.mafazaa.ainaa.domain.model.Report
import com.mafazaa.ainaa.domain.model.Version
import kotlinx.coroutines.flow.Flow
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