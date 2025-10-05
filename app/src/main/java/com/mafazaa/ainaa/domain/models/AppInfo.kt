package com.mafazaa.ainaa.domain.models

import android.graphics.drawable.Drawable

/**
 * Data class for BlockAppDialog.
 */
data class AppInfo(
    val name: String,
    val icon: Drawable?,
    val packageName: String,
    val isSelected: Boolean = false,
)