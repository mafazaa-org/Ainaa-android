package com.mafazaa.ainaa.model.repo

import com.mafazaa.ainaa.data.remote.NetworkResult
import com.mafazaa.ainaa.model.ReportDto
import com.mafazaa.ainaa.model.VersionDto
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RemoteRepo {
    fun submitReportToGoogleForm(reportDto: ReportDto): Flow<NetworkResult>
    suspend fun getLatestVersion(): VersionDto?
    suspend fun downloadFile(
        url: String,
        file: File,
    ): Boolean


}