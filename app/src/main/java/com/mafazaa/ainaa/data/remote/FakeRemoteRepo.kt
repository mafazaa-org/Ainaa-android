package com.mafazaa.ainaa.data.remote

import com.mafazaa.ainaa.model.ReportDto
import com.mafazaa.ainaa.model.repo.RemoteRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * we use this fake repo in debug builds to avoid actual forms submissions
 * but the update check and download is real
 */
object FakeRemoteRepo : RemoteRepo by KtorRepo() {

}