package com.mafazaa.ainaa.model

sealed class UpdateState {
    object NoUpdate : UpdateState()
    object Checking : UpdateState()
    object Downloading : UpdateState()
    object Downloaded : UpdateState()
    data class Failed(val error: String? = null) : UpdateState()
}
