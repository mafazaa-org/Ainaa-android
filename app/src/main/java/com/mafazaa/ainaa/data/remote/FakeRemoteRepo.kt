package com.mafazaa.ainaa.data.remote

import com.mafazaa.ainaa.model.Report
import com.mafazaa.ainaa.model.repo.RemoteRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object FakeRemoteRepo: RemoteRepo by KtorRepo() {
    override fun submitPhoneNumberToGoogleForm(phoneNumber: String): Flow<NetworkResult> = flow {
        emit(NetworkResult.Loading)
        delay(200)
        emit(NetworkResult.Success)
    }

    override fun submitReportToGoogleForm(report: Report): Flow<NetworkResult> = flow {
        emit(NetworkResult.Loading)
        delay(200)
        emit(NetworkResult.Success)
    }


}