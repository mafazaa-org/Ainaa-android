package com.mafazaa.ainaa.domain.repo

import com.mafazaa.ainaa.data.models.NetworkResult
import com.mafazaa.ainaa.data.models.ReportModel
import com.mafazaa.ainaa.data.models.VersionModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RemoteRepo {
    fun submitReportToGoogleForm(reportModel: ReportModel): Flow<NetworkResult>
    suspend fun getLatestVersion(): VersionModel?
    suspend fun downloadFile(
        url: String,
        file: File,
    ): Boolean


}