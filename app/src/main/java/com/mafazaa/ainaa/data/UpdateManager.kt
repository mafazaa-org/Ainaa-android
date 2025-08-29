package com.mafazaa.ainaa.data

import android.util.*
import com.mafazaa.ainaa.*
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.model.repo.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// class UpdateManager(
//     private val repo: RemoteRepo,
//     private val localData: LocalData,
//     private val fileProvider: FileRepo,
// ): UpdateRepo {
//     // suspend function that does the heavy lifting, caller controls coroutine scope
//     override fun checkAndDownloadIfNeeded(currentVersion: Int): Flow<UpdateState> = flow {
//         emit(UpdateState.Checking)

//         val updateFile = fileProvider.getUpdateFile()

//         val version = try {
//             repo.getLatestVersion()
//         } catch (_: Exception) {
//             emit(UpdateState.NoUpdate)
//             return@flow
//         }

//         if (version == null) {
//             emit(
//                 if (localData.downloadedVersion > currentVersion) UpdateState.Downloaded
//                 else UpdateState.NoUpdate
//             )
//             return@flow
//         }

//         if (localData.downloadedVersion >= version.version) {
//             emit(UpdateState.Downloaded)
//             return@flow
//         }

//         if (version.version <= currentVersion) {
//             emit(UpdateState.NoUpdate)
//             return@flow
//         }

//         // need to download

//         Lg.d(TAG, "Downloading update version ${version.version}")
//         emit(UpdateState.Downloading)
//         if (repo.downloadFile(version.downloadUrl, updateFile)) {
//             localData.downloadedVersion = version.version
//             emit(UpdateState.Downloaded)
//         } else {
//             Lg.e(TAG, "Download failed")
//             emit(UpdateState.Failed("Download failed"))
//         }
//     }.flowOn(Dispatchers.IO)

//     companion object {
//         const val TAG = "UpdateManager"
//     }

// }