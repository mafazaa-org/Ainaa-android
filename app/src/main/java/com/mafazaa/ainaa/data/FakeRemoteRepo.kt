package com.mafazaa.ainaa.data

import com.mafazaa.ainaa.model.Report
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object FakeRemoteRepo:RemoteRepo {
    override fun submitPhoneNumberToGoogleForm(phoneNumber: String): Flow<SubmitResult> =flow {
        emit(SubmitResult.Loading)
        delay(200)
        emit(SubmitResult.Success)
    }

    override fun submitReportToGoogleForm(report: Report): Flow<SubmitResult> =flow {
        emit(SubmitResult.Loading)
        delay(200)
        emit(SubmitResult.Success)
    }
}