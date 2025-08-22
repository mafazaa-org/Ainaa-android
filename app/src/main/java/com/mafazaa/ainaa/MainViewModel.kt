package com.mafazaa.ainaa

import android.util.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.model.*
import com.mafazaa.ainaa.model.repo.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

class MainViewModel(
    private val remoteRepo: RemoteRepo,
    private val localData: LocalData,
    private val fileRepo: FileRepo ,
    private val updateRepo: UpdateRepo
): ViewModel() {

    private val TAG = "MainViewModel"
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    var updateState = mutableStateOf<UpdateState>(UpdateState.NoUpdate)
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

    fun submitPhoneNumber(phoneNumber: String, result: (NetworkResult) -> Unit) {
        viewModelScope.launch {
            remoteRepo.submitPhoneNumberToGoogleForm(phoneNumber)
                .collect { result ->
                    result(result)
                }
        }
    }

    fun submitReport(report: Report, result: (NetworkResult) -> Unit) {
        viewModelScope.launch {
            remoteRepo.submitReportToGoogleForm(report)
                .collect { submitResult ->
                    Lg.d(TAG, submitResult.toString())
                    result(submitResult)
                }
        }
    }

    fun handleUpdateStatus(){
        viewModelScope.launch {
            updateRepo.checkAndDownloadIfNeeded(BuildConfig.VERSION_CODE).collect {
                updateState.value = it
                Log.d(TAG, "Update state: $it")
            }
        }
    }
    fun getLogFile(): File {
        return fileRepo.getLogFile()
    }

    fun saveLevel(level: ProtectionLevel) {
        localData.level = level
    }

    fun savePhoneNumber(phoneNumber: String) {
        localData.phoneNum = phoneNumber
    }

    fun updateFile(): File {
        return fileRepo.getUpdateFile()
    }

}
