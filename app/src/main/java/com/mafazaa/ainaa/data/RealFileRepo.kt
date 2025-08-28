package com.mafazaa.ainaa.data

import android.content.*
import com.mafazaa.ainaa.model.*
import java.io.*

class RealFileRepo(val context: Context): FileRepo {

    override fun getUpdateFile(): File = File(
        context.filesDir,
        "update.apk"
    )

    override fun getLogFile(): File = File(context.cacheDir, "log.txt")
    override fun getLogSize(): Long {
        return getLogFile().length()
    }

    override fun wipeLog() {
        val logFile = getLogFile()
        if (logFile.exists()) {
            logFile.writeText("") // Clear the log file
        } else {
            logFile.createNewFile() // Create a new file if it doesn't exist
        }
    }

    override fun saveToLog(content: String) {
        val logFile = getLogFile()
        logFile.appendText(content)
    }
}