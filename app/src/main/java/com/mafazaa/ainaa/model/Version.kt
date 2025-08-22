package com.mafazaa.ainaa.model

data class Version(
    val version: Int,
    val name: String,
    val downloadUrl: String,
    val body: String,
    val size: Long,
)