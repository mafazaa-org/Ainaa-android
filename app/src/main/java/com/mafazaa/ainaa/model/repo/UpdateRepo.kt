package com.mafazaa.ainaa.model.repo

import com.mafazaa.ainaa.model.UpdateState
import kotlinx.coroutines.flow.Flow

interface UpdateRepo {
    fun checkAndDownloadIfNeeded(currentVersion: Int): Flow<UpdateState>
}
