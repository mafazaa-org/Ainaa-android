package com.mafazaa.ainaa.model.repo

import com.mafazaa.ainaa.model.ScriptCode
import com.mafazaa.ainaa.model.ScreenAnalysis
import com.mafazaa.ainaa.model.ScriptResult

interface ScriptRepo {
    fun setCodes(codes: List<ScriptCode>)
    fun evaluate(screenAnalysis: ScreenAnalysis): ScriptResult
}

