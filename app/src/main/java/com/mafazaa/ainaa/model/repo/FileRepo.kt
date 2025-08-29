package com.mafazaa.ainaa.model

import java.io.File

interface FileRepo {
    // fun getUpdateFile(): File
    fun getLogFile(): File
    fun saveToLog(content: String)

    fun getLogSize(): Long
    fun wipeLog()
}
