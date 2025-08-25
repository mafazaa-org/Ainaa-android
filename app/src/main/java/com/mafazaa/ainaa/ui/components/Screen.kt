package com.mafazaa.ainaa.ui.components

sealed class Screen(val route: String) {
    object ProtectionActivated: Screen("protection_activated")
    object Support: Screen("support")
    object EnableProtection: Screen("enable_protection")
}
