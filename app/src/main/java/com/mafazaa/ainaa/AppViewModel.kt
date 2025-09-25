package com.mafazaa.ainaa

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mafazaa.ainaa.data.local.LocalData
import com.mafazaa.ainaa.data.local.add
import com.mafazaa.ainaa.data.local.remove
import com.mafazaa.ainaa.data.remote.NetworkResult
import com.mafazaa.ainaa.model.DnsProtectionLevel
import com.mafazaa.ainaa.model.FileRepo
import com.mafazaa.ainaa.model.ReportDto
import com.mafazaa.ainaa.model.UpdateState
import com.mafazaa.ainaa.model.repo.RemoteRepo
import com.mafazaa.ainaa.model.repo.UpdateRepo
import com.mafazaa.ainaa.ui.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AppViewModel(
    private val remoteRepo: RemoteRepo,
    private val localData: LocalData,
    private val fileRepo: FileRepo,
    private val updateRepo: UpdateRepo
) : ViewModel() {

    private val TAG = "MainViewModel"
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    var updateState = mutableStateOf<UpdateState>(UpdateState.NoUpdate)
    fun loadInstalledApps(appList: List<AppInfo>) {
        val appList = appList.toMutableList()
        val selectedApps = localData.blockedApps
        for (selectedApp in selectedApps) {
            val i = appList.indexOfFirst { selectedApp == it.packageName }
            if (i != -1) {
                appList[i] = appList[i].copy(isSelected = true)
            }
        }
        _apps.value = appList
    }

    fun handleUpdateStatus() {
        viewModelScope.launch {
            updateRepo.checkAndDownloadIfNeeded(BuildConfig.VERSION_CODE).collect {
                updateState.value = it
                Log.d(TAG, "Update state: $it")
            }
        }
    }

    fun toggleAppSelection(packageName: String) {
        if (localData.blockedApps.firstOrNull { it == packageName } == null) {
            localData.blockedApps = localData.blockedApps.add(packageName)
        } else {
            localData.blockedApps = localData.blockedApps.remove(packageName)
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

    fun submitReport(reportDto: ReportDto, result: (NetworkResult) -> Unit) {
        viewModelScope.launch {
            remoteRepo.submitReportToGoogleForm(reportDto)
                .collect { submitResult ->
                    Lg.d(TAG, submitResult.toString())
                    result(submitResult)
                }
        }
    }

    fun getLogFile(): File {
        return fileRepo.getLogFile()
    }

    fun updateFile(): File {
        return fileRepo.getUpdateFile()
    }

    fun saveLevel(level: DnsProtectionLevel) {
        localData.dnsProtectionLevel = level
    }

    fun savePhoneNumber(phoneNumber: String) {
        localData.phoneNum = phoneNumber
    }


}
