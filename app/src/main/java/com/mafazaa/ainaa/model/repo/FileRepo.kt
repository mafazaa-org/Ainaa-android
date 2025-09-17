package com.mafazaa.ainaa.model

import java.io.File

interface FileRepo {
    companion object {
        const val defaultLogFileName = "log.txt"
    }

    // fun getUpdateFile(): File
    fun getLogFile(fileName: String = defaultLogFileName): File
    fun saveToLog(content: String, fileName: String = defaultLogFileName)

    fun getLogSize(fileName: String = defaultLogFileName): Long
    fun wipeLog(fileName: String = defaultLogFileName)
}
