package com.mafazaa.ainaa.model

data class VersionDto(
    val version: Int,
    val name: String,
    val downloadUrl: String,
    val body: String,
    val size: Long,
)