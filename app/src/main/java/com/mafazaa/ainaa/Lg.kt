package com.mafazaa.ainaa

import android.util.*
import com.mafazaa.ainaa.data.*
import com.mafazaa.ainaa.model.*

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
}
