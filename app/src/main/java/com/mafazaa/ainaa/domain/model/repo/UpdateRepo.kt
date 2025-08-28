package com.mafazaa.ainaa.domain.model.repo

import com.mafazaa.ainaa.domain.model.UpdateState
import kotlinx.coroutines.flow.Flow

interface UpdateRepo {
    fun checkAndDownloadIfNeeded(currentVersion: Int): Flow<UpdateState>
}
