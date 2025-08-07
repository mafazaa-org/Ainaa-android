package com.mafazaa.ainaa

import android.util.*
import androidx.lifecycle.*
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MainViewModel(
    private val remoteRepo: RemoteRepo,
    private val localData: LocalData,
): ViewModel() {

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

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
        if(localData.apps.firstOrNull{it==packageName}==null){
            localData.apps=localData.apps.add(packageName)
        }else{
            localData.apps=localData.apps.remove(packageName)
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
                    Log.d("Temp", submitResult.toString())
                    result(submitResult)
                }
        }
    }

    fun savePhoneNumber(phoneNumber: String) {
        localData.phoneNum
    }

}
