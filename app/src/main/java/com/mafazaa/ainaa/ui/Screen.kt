package com.mafazaa.ainaa.ui

sealed class Screen() {
    object ProtectionActivated : Screen()
    object Support : Screen()
    object EnableProtection : Screen()
}
