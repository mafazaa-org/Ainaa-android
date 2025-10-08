package com.mafazaa.ainaa.data

import com.mafazaa.ainaa.utils.MyLog
import com.mafazaa.ainaa.data.local.SharedPrefs
import com.mafazaa.ainaa.domain.FileRepo
import com.mafazaa.ainaa.domain.models.UpdateState
import com.mafazaa.ainaa.domain.repo.RemoteRepo
import com.mafazaa.ainaa.domain.repo.UpdateRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UpdateManager(
    private val repo: RemoteRepo,
    private val sharedPrefs: SharedPrefs,
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
                if (sharedPrefs.downloadedVersion > currentVersion) {
                    UpdateState.Downloaded
                } else {
                    UpdateState.NoUpdate
                }
            )
            return@flow
        }

        if (sharedPrefs.downloadedVersion >= remoteVersion.version) {
            emit(UpdateState.Downloaded)
            return@flow
        }

        if (remoteVersion.version <= currentVersion) {
            emit(UpdateState.NoUpdate)
            return@flow
        }

        MyLog.d(TAG, "Downloading update version ${remoteVersion.version}")
        emit(UpdateState.Downloading)
        if (repo.downloadFile(remoteVersion.downloadUrl, updateFile)) {
            sharedPrefs.downloadedVersion = remoteVersion.version
            emit(UpdateState.Downloaded)
        } else {
            MyLog.e(TAG, "Download failed")
            emit(UpdateState.Failed("Download failed"))
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        const val TAG = "UpdateManager"
    }

}