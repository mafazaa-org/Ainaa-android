package com.mafazaa.ainaa.model.repo

import com.mafazaa.ainaa.data.remote.NetworkResult
import com.mafazaa.ainaa.model.Report
import kotlinx.coroutines.flow.Flow

interface RemoteRepo {
    fun submitPhoneNumberToGoogleForm(phoneNumber: String): Flow<NetworkResult>
    fun submitReportToGoogleForm(report: Report): Flow<NetworkResult>


}