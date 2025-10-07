package com.mafazaa.ainaa.data.remote

import com.mafazaa.ainaa.domain.repo.RemoteRepo

/**
 * we use this fake repo in debug builds to avoid actual forms submissions
 * but the update check and download is real
 */
object FakeRemoteRepo : RemoteRepo by KtorRepo() {

}