package com.mafazaa.ainaa.data.repository_impl.view_models.state

sealed class SubmitResult {
    object Loading: SubmitResult()
    object Success: SubmitResult()
    data class Error(val message: String?): SubmitResult()
}