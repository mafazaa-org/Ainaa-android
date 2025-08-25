package com.mafazaa.ainaa.data.repository_impl.data_source.remote_data_source

import com.mafazaa.ainaa.data.repository_impl.view_models.state.SubmitResult
import com.mafazaa.ainaa.domain.model.Report
import com.mafazaa.ainaa.domain.model.Version
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

object FakeRemoteRepo: RemoteRepo {
    override fun submitPhoneNumberToGoogleForm(phoneNumber: String): Flow<SubmitResult> = flow {
        emit(SubmitResult.Loading)
        delay(200)
        emit(SubmitResult.Success)
    }

    override fun submitReportToGoogleForm(report: Report): Flow<SubmitResult> = flow {
        emit(SubmitResult.Loading)
        delay(200)
        emit(SubmitResult.Success)
    }

    override suspend fun getLatestVersion(): Version? {
       return null
    }

    override suspend fun downloadFile(url: String, file: File): Boolean {
        return false
    }
}