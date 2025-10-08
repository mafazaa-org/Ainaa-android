package com.mafazaa.ainaa.data.models

data class VersionModel(
    val version: Int,
    val name: String,
    val downloadUrl: String,
    val body: String,
    val size: Long,
)