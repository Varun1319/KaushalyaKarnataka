package com.kaushalya.karnataka.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Auth        : Screen("auth")
    object Home        : Screen("home")
    object Profile     : Screen("profile/{workerId}") {
        fun createRoute(id: String) = "profile/$id"
    }
    object AiBuilder   : Screen("ai_builder")
    object Dashboard   : Screen("dashboard")
}

data class BottomNavItem(
    val screen : Screen,
    val label  : String,
    val icon   : ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home,      "Home",       Icons.Filled.Home),
    BottomNavItem(Screen.Home,      "Search",     Icons.Filled.Search),   // triggers search mode
    BottomNavItem(Screen.AiBuilder, "AI Builder", Icons.Filled.AutoAwesome),
    BottomNavItem(Screen.Dashboard, "Dashboard",  Icons.Filled.Dashboard),
)
