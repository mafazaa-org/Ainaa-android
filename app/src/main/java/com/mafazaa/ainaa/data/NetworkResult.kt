package com.mafazaa.ainaa.data

sealed class NetworkResult {
    object Loading: NetworkResult()
    object Success: NetworkResult()
    data class Error(val message: String?): NetworkResult()
}