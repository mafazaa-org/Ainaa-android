package com.mafazaa.ainaa.data

sealed class SubmitResult {
    object Loading: SubmitResult()
    object Success: SubmitResult()
    data class Error(val message: String?): SubmitResult()
}