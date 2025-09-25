package com.mafazaa.ainaa.model

sealed class ScriptResult {
    data class Success(val scriptName: String, val matched: Boolean) : ScriptResult()
    data class Error(val error: String) : ScriptResult()
}