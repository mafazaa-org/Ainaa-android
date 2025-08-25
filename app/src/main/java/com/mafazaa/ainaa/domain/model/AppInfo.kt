package com.mafazaa.ainaa.domain.model

import android.graphics.drawable.*

data class AppInfo(
    val name: String,
    val icon: Drawable?,
    val packageName: String,
    val isSelected: Boolean = false,
)