package com.mafazaa.ainaa

import android.util.Log
import com.mafazaa.ainaa.data.FakeFileRepo
import com.mafazaa.ainaa.model.ScreenAnalysis
import com.mafazaa.ainaa.model.FileRepo
import java.io.File

object Lg {
    var fileRepo: FileRepo = FakeFileRepo // inject real one later
    const val LOG_FILE_SIZE_LIMIT = 1024 * 1024 * 10 // 5 MB
    private fun logToFile(level: String, tag: String, msg: String, tr: Throwable? = null) {
        if (msg.isBlank()) return
        if (fileRepo.getLogSize() > LOG_FILE_SIZE_LIMIT) {
            fileRepo.wipeLog() // clear log if it exceeds size limit
        }
        val time = System.currentTimeMillis()
        val formatedTime = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", time)
        val logLine = buildString {
            append("$formatedTime|$tag: $msg\n")
            if (tr != null) {
                append("-".repeat(15) + "\n")
                append(tr.stackTraceToString())
                append("-".repeat(15) + "\n")
            }
        }

        fileRepo.saveToLog(logLine)
    }

    fun d(tag: String, msg: String, tr: Throwable? = null): Int {
        val res = if (tr != null) Log.d(tag, msg, tr) else Log.d(tag, msg)
        logToFile("DEBUG", tag, msg, tr)
        return res
    }

    fun i(tag: String, msg: String, tr: Throwable? = null): Int {
        val res = if (tr != null) Log.i(tag, msg, tr) else Log.i(tag, msg)
        logToFile("INFO", tag, msg, tr)
        return res
    }

    fun w(tag: String, msg: String, tr: Throwable? = null): Int {
        val res = if (tr != null) Log.w(tag, msg, tr) else Log.w(tag, msg)
        logToFile("WARN", tag, msg, tr)
        return res
    }

    fun e(tag: String, msg: String, tr: Throwable? = null): Int {
        val res = if (tr != null) Log.e(tag, msg, tr) else Log.e(tag, msg)
        logToFile("ERROR", tag, msg, tr)
        return res
    }

    fun logUiTree(codeName:String,screenAnalysis: ScreenAnalysis): File {
        val pkg = screenAnalysis.pkg ?: "unknown"
        val fileName = "$codeName.txt"
        val logFile = fileRepo.getLogFile(fileName)
        fileRepo.wipeLog(fileName)
        fileRepo.saveToLog((screenAnalysis).toString(), fileName)
        return logFile
    }

    fun logWithInfo(packageName:String): File {//todo log the reason
        val fileName = "$packageName.txt"
        val logFile = fileRepo.getLogFile(fileName)
        fileRepo.wipeLog(fileName)
        val info = buildString {
            append("Package: $packageName\n")
            append("Log file size: ${fileRepo.getLogSize()} bytes\n")
            append("Time: ${android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis())}\n")
        }
        fileRepo.saveToLog(info, fileName)
        return logFile

    }
}
