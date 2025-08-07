package com.mafazaa.ainaa.data

import com.mafazaa.ainaa.model.*
import kotlinx.coroutines.flow.*

interface RemoteRepo {
    fun submitPhoneNumberToGoogleForm(phoneNumber: String): Flow<SubmitResult>
    fun submitReportToGoogleForm(report: Report): Flow<SubmitResult>
}