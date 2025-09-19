package com.mafazaa.ainaa.data

import com.mafazaa.ainaa.model.FileRepo
import java.io.File

object FakeFileRepo: FileRepo {
    // override fun getUpdateFile(): File
    override fun getLogFile(fileName: String) = File("fake")
    override fun getUpdateFile(): File = File("fake")


    override fun saveToLog(content: String, fileName: String) {
    }

    override fun getLogSize(fileName: String): Long {
        return 0L
    }

    override fun wipeLog(fileName: String) {
    }


}