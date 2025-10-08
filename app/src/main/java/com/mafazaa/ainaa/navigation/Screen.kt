package com.mafazaa.ainaa.navigation

sealed class Screen() {
    object ProtectionActivated : Screen()
    object Support : Screen()
    object EnableProtection : Screen()
}