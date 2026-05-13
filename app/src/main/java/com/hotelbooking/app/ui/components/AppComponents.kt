package com.hotelbooking.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

val CyanMain = Color(0xFF00E5FF)
val CyanLight = Color(0xFFE0F7FA)

enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    SEARCH("home", "Search", Icons.Filled.Search),
    BOOKINGS("bookings", "Bookings", Icons.Filled.DateRange),
    CHAT("chat", "Chat", Icons.Filled.Chat),
    ADMIN("admin", "Admin", Icons.Filled.VerifiedUser),
    SETTINGS("settings", "Settings", Icons.Filled.Settings)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        BottomNavItem.entries.forEach { item ->
            val isSelected = item.route == currentRoute
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = { onItemClick(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CyanMain,
                    selectedTextColor = CyanMain,
                    indicatorColor = CyanLight
                )
            )
        }
    }
}
