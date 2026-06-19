package com.hotelbooking.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.screens.notification.NotificationViewModel
import com.hotelbooking.app.ui.theme.AppColors

// Backward-compatible aliases
val CyanMain = AppColors.SkyBrand
val CyanLight = AppColors.CyanSurface

enum class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    SEARCH("home", "Search", Icons.Filled.Search, Icons.Outlined.Search),
    BOOKINGS(Routes.MY_BOOKINGS, "Bookings", Icons.Filled.DateRange, Icons.Outlined.DateRange),
    MESSAGE(Routes.CHAT_LIST, "Message", Icons.Filled.Message, Icons.Outlined.Message),
    NOTIFICATION(Routes.NOTIFICATIONS, "Alerts", Icons.Filled.Notifications, Icons.Outlined.Notifications),
    SETTINGS(Routes.PROFILE_SETTING, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun AppBottomNavBar(
    isDarkMode: Boolean,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val unreadCount = NotificationViewModel.sharedUnreadCount.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .offset(y = (-8).dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        AppColors.NavyDeep.copy(alpha = 0.95f),
                        AppColors.NavyMid.copy(alpha = 0.95f)
                    )
                )
            )
            .drawBehind {
                // Top edge inner highlight
                drawLine(
                    color = Color.White.copy(alpha = 0.12f),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 0.5.dp.toPx()
                )
                // Subtle bottom inner shadow
                drawLine(
                    color = Color.Black.copy(alpha = 0.15f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 0.5.dp.toPx()
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem.entries.forEach { item ->
                val isSelected = currentRoute == item.route
                PremiumNavItem(
                    item = item,
                    isSelected = isSelected,
                    unreadCount = if (item == BottomNavItem.NOTIFICATION) unreadCount else 0,
                    onClick = { if (!isSelected) onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun PremiumNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    unreadCount: Int,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.18f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "nav_icon_scale"
    )

    val labelAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.5f,
        animationSpec = tween(200),
        label = "nav_label_alpha"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.25f else 0f,
        animationSpec = tween(250),
        label = "nav_glow"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Glow halo behind active icon
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AppColors.GoldPrimary.copy(alpha = glowAlpha),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )
            }

            // Icon
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    },
                tint = if (isSelected) AppColors.GoldPrimary else Color.White.copy(alpha = 0.5f)
            )

            // Notification badge dot
            if (item == BottomNavItem.NOTIFICATION && unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .background(Color(0xFFF44336), CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = item.label,
            fontSize = 9.5.sp,
            fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold
            else androidx.compose.ui.text.font.FontWeight.Medium,
            color = if (isSelected) AppColors.GoldPrimary else Color.White.copy(alpha = 0.45f),
            modifier = Modifier.graphicsLayer { this.alpha = labelAlpha }
        )
    }
}

// Legacy component for backward compatibility
@Composable
fun BottomNavBar(
    currentRoute: String,
    isDarkMode: Boolean = false,
    onItemClick: (BottomNavItem) -> Unit
) {
    AppBottomNavBar(
        isDarkMode = isDarkMode,
        currentRoute = currentRoute,
        onNavigate = { route ->
            BottomNavItem.entries.find { it.route == route }?.let { onItemClick(it) }
        }
    )
}
