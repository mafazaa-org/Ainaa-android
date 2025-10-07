package com.mafazaa.ainaa.domain.repo

import com.mafazaa.ainaa.domain.models.UpdateState
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for handling app update logic.
 *
 * Implementations of this interface are responsible for checking if a new version
 * of the app is available and downloading it if necessary. The result of the
 * update process is emitted as a [Flow] of [UpdateState] values, allowing
 * consumers to observe the update progress and state changes asynchronously.
 */
interface UpdateRepo {
    /**
     * Checks if an update is needed based on the current version and downloads it if required.
     *
     * @param currentVersion The current version code of the app.
     * @return A [Flow] emitting [UpdateState] values representing the update process state.
     */
    fun checkAndDownloadIfNeeded(currentVersion: Int): Flow<UpdateState>
}
