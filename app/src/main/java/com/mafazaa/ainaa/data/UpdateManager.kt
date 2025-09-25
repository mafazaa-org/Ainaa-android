package com.mafazaa.ainaa.data

import com.mafazaa.ainaa.Lg
import com.mafazaa.ainaa.data.local.LocalData
import com.mafazaa.ainaa.model.FileRepo
import com.mafazaa.ainaa.model.UpdateState
import com.mafazaa.ainaa.model.repo.RemoteRepo
import com.mafazaa.ainaa.model.repo.UpdateRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UpdateManager(
    private val repo: RemoteRepo,
    private val localData: LocalData,
    private val fileProvider: FileRepo,
) : UpdateRepo {

    override fun checkAndDownloadIfNeeded(currentVersion: Int): Flow<UpdateState> = flow {
        emit(UpdateState.Checking)

        val updateFile = fileProvider.getUpdateFile()

        val remoteVersion = try {
            repo.getLatestVersion()
        } catch (_: Exception) {
            emit(UpdateState.NoUpdate)
            return@flow
        }

        if (remoteVersion == null) {
            emit(
                if (localData.downloadedVersion > currentVersion) {
                    UpdateState.Downloaded
                } else {
                    UpdateState.NoUpdate
                }
            )
            return@flow
        }

        if (localData.downloadedVersion >= remoteVersion.version) {
            emit(UpdateState.Downloaded)
            return@flow
        }

        if (remoteVersion.version <= currentVersion) {
            emit(UpdateState.NoUpdate)
            return@flow
        }

        Lg.d(TAG, "Downloading update version ${remoteVersion.version}")
        emit(UpdateState.Downloading)
        if (repo.downloadFile(remoteVersion.downloadUrl, updateFile)) {
            localData.downloadedVersion = remoteVersion.version
            emit(UpdateState.Downloaded)
        } else {
            Lg.e(TAG, "Download failed")
            emit(UpdateState.Failed("Download failed"))
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        const val TAG = "UpdateManager"
    }

}