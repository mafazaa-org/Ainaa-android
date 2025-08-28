package com.mafazaa.ainaa.data

import com.mafazaa.ainaa.domain.model.repo.FileRepo
import java.io.File

object FakeFileRepo: FileRepo {
    override fun getUpdateFile(): File =File("fake")

    override fun getLogFile(): File = File("fake")

    override fun saveToLog(content: String) {
    }

    override fun getLogSize(): Long {
        return 0L
    }

    override fun wipeLog() {

    }

}