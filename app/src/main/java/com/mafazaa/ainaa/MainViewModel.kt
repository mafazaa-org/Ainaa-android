package com.mafazaa.ainaa

import android.content.*
import android.util.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainViewModel(
    private val remoteRepo: RemoteRepo,
    private val localData: LocalData,
): ViewModel() {
    private val TAG = "MainViewModel"

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    var updateStatus = mutableStateOf(UpdateStatus.NO_UPDATE)
    fun loadInstalledApps(appList: List<AppInfo>) {
        val appList = appList.toMutableList()
        val selectedApps = localData.apps
        for (selectedApp in selectedApps) {
            val i = appList.indexOfFirst { selectedApp == it.packageName }
            if (i != -1) {
                appList[i] = appList[i].copy(isSelected = true)
            }
        }
        _apps.value = appList
    }

    fun toggleAppSelection(packageName: String) {
        if (localData.apps.firstOrNull { it == packageName } == null) {
            localData.apps = localData.apps.add(packageName)
        } else {
            localData.apps = localData.apps.remove(packageName)
        }
        _apps.value = _apps.value.map {
            if (it.packageName == packageName) it.copy(isSelected = !it.isSelected) else it
        }
    }

    fun submitPhoneNumber(phoneNumber: String, result: (SubmitResult) -> Unit) {
        viewModelScope.launch {
            remoteRepo.submitPhoneNumberToGoogleForm(phoneNumber)
                .collect { result ->
                    result(result)
                }
        }
    }

    fun submitReport(report: Report, result: (SubmitResult) -> Unit) {
        viewModelScope.launch {
            remoteRepo.submitReportToGoogleForm(report)
                .collect { submitResult ->
                    Log.d(TAG, submitResult.toString())
                    result(submitResult)
                }
        }
    }

    fun onUpdateClicked(context: Context) {
        when (updateStatus.value) {
            UpdateStatus.DOWNLOADED -> {
                context.installApk(context.updateFile())
            }

            UpdateStatus.FAILED -> {
                Log.e(TAG, "Update download failed, retrying...")
                handleUpdateStatus(context)
            }

            UpdateStatus.NO_UPDATE -> {
                Log.d(TAG, "No update available")
                handleUpdateStatus(context)
            }

            else -> {
            }
        }
    }

    fun handleUpdateStatus(context: Context) {

        CoroutineScope(Dispatchers.IO).launch {
            val updateFile = context.updateFile()
            if (updateFile.exists()) {
                updateStatus.value = UpdateStatus.DOWNLOADED
                Log.d(TAG, "Update already downloaded, size: ${updateFile.length()}")
                return@launch
            }
            val version = remoteRepo.getLatestVersion()
            if (version == null || version.version <= BuildConfig.VERSION_CODE) {
                updateStatus.value = UpdateStatus.NO_UPDATE
                Log.d(TAG, "No update available, remote version: ${version?.version}")
                return@launch
            } else {
                updateStatus.value = UpdateStatus.DOWNLOADING
                Log.d(TAG, "New update available: ${version.version}, downloading...")
                val res = remoteRepo.downloadFile(version.downloadUrl, updateFile)
                if (res) {
                    updateStatus.value = UpdateStatus.DOWNLOADED
                    Log.d(TAG, "Update downloaded successfully,size: ${updateFile.length()}")
                } else {
                    updateStatus.value = UpdateStatus.FAILED
                    Log.e(TAG, "Update download failed")
                }
            }
        }
    }

    fun savePhoneNumber(phoneNumber: String) {
        localData.phoneNum = phoneNumber
    }
}