package com.hotelbooking.app.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hotelbooking.app.data.model.Notification
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import com.hotelbooking.app.util.TokenManager
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    isDarkMode: Boolean = false,
    onNavigateToBookings: () -> Unit = {},
    viewModel: NotificationViewModel = viewModel()
) {

    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // FIX CURRENT ROUTE
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        TokenManager.getUserId()?.let {
            viewModel.setUserId(it)
        }
    }

    val textColor = AppColors.textPrimary(isDarkMode)
    val subTextColor = AppColors.textSecondary(isDarkMode)
    val bgColor = AppColors.background(isDarkMode)

    val hasUnread = unreadCount > 0

    Scaffold(
        containerColor = bgColor,

        bottomBar = {

            NotificationBottomNav(
                isDarkMode = isDarkMode,
                currentRoute = currentRoute ?: Routes.NOTIFICATIONS
            ) { route ->

                if (route != currentRoute) {

                    navController.navigate(route) {

                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }

    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = {
                viewModel.fetchNotifications()
            },

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),

                        verticalAlignment = Alignment.CenterVertically,

                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = null,
                                tint = AppColors.CyanMain,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = "Notifications",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            if (hasUnread) {

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Badge(
                                    containerColor = Color(0xFFF44336)
                                ) {

                                    Text(
                                        text =
                                            if (unreadCount > 99)
                                                "99+"
                                            else
                                                unreadCount.toString(),

                                        color = Color.White,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        if (hasUnread) {

                            TextButton(
                                onClick = {
                                    viewModel.markAllAsRead()
                                }
                            ) {

                                Text(
                                    text = "Mark all read",
                                    fontSize = 13.sp,
                                    color = AppColors.CyanMain
                                )
                            }
                        }
                    }
                }

                // ERROR
                if (error != null && notifications.isEmpty()) {

                    item {

                        Card(
                            modifier = Modifier.fillMaxWidth(),

                            shape = RoundedCornerShape(16.dp),

                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFEBEE)
                            )
                        ) {

                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    Icons.Filled.ErrorOutline,
                                    contentDescription = null,
                                    tint = Color(0xFFF44336),
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(
                                    modifier = Modifier.width(8.dp)
                                )

                                Text(
                                    text = error ?: "Failed to load notifications",
                                    fontSize = 13.sp,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }

                // EMPTY
                if (!isLoading && notifications.isEmpty() && error == null) {

                    item {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),

                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Icon(
                                Icons.Filled.NotificationsNone,
                                contentDescription = null,

                                modifier = Modifier.size(72.dp),

                                tint = subTextColor.copy(alpha = 0.4f)
                            )

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Text(
                                text = "No notifications yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "You'll receive booking confirmations and admin messages here",
                                fontSize = 14.sp,
                                color = subTextColor,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                // LIST
                items(
                    notifications,
                    key = { it.id }
                ) { notification ->

                    NotificationCard(
                        notification = notification,
                        isDarkMode = isDarkMode,

                        onClick = {

                            if (!notification.is_read) {
                                viewModel.markAsRead(notification.id)
                            }

                            if (notification.type == "BOOKING_CONFIRMED") {
                                onNavigateToBookings()
                            }
                        }
                    )
                }

                item {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: Notification,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {

    val textColor = AppColors.textPrimary(isDarkMode)
    val subTextColor = AppColors.textSecondary(isDarkMode)
    val surfaceColor = AppColors.surface(isDarkMode)

    val (icon, iconColor, bgColor) = when (notification.type) {

        "BOOKING_CONFIRMED" -> Triple(
            Icons.Filled.CheckCircle,
            Color(0xFF4CAF50),
            Color(0xFFE8F5E9)
        )

        "ADMIN_MESSAGE" -> Triple(
            Icons.Filled.Campaign,
            Color(0xFF2196F3),
            Color(0xFFE3F2FD)
        )

        else -> Triple(
            Icons.Filled.Notifications,
            AppColors.CyanMain,
            Color(0xFFE0F7FA)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation =
                if (notification.is_read)
                    1.dp
                else
                    3.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),

            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(bgColor),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),

                    horizontalArrangement = Arrangement.SpaceBetween,

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = notification.title,

                        fontSize = 15.sp,

                        fontWeight =
                            if (notification.is_read)
                                FontWeight.Normal
                            else
                                FontWeight.Bold,

                        color = textColor,

                        maxLines = 1,

                        overflow = TextOverflow.Ellipsis,

                        modifier = Modifier.weight(1f)
                    )

                    if (!notification.is_read) {

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF44336))
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = notification.body,
                    fontSize = 13.sp,
                    color = subTextColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = formatRelativeTime(notification.created_at),
                    fontSize = 11.sp,
                    color = subTextColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun NotificationBottomNav(
    isDarkMode: Boolean,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {

    NavigationBar(
        containerColor = AppColors.surface(isDarkMode),
        tonalElevation = if (isDarkMode) 0.dp else 8.dp
    ) {

        // HOME
        NavigationBarItem(

            selected = currentRoute == Routes.HOME,

            onClick = {
                onNavigate(Routes.HOME)
            },

            alwaysShowLabel = true,

            icon = {

                Icon(
                    imageVector =
                        if (currentRoute == Routes.HOME)
                            Icons.Filled.Home
                        else
                            Icons.Outlined.Home,

                    contentDescription = null
                )
            },

            label = {
                Text(
                    text = "Home",
                    fontSize = 11.sp
                )
            }
        )

        // BOOKINGS
        NavigationBarItem(

            selected = currentRoute == Routes.MY_BOOKINGS,

            onClick = {
                onNavigate(Routes.MY_BOOKINGS)
            },

            alwaysShowLabel = true,

            icon = {

                Icon(
                    imageVector =
                        if (currentRoute == Routes.MY_BOOKINGS)
                            Icons.Filled.DateRange
                        else
                            Icons.Outlined.Book,

                    contentDescription = null
                )
            },

            label = {
                Text(
                    text = "Bookings",
                    fontSize = 11.sp
                )
            }
        )

        // NOTIFICATIONS
        NavigationBarItem(

            selected = currentRoute == Routes.NOTIFICATIONS,

            onClick = {
                onNavigate(Routes.NOTIFICATIONS)
            },

            alwaysShowLabel = true,

            icon = {

                Icon(
                    imageVector =
                        if (currentRoute == Routes.NOTIFICATIONS)
                            Icons.Filled.Notifications
                        else
                            Icons.Outlined.Notifications,

                    contentDescription = null
                )
            },

            label = {
                Text(
                    text = "Notification",
                    fontSize = 11.sp
                )
            }
        )

        // SETTINGS
        NavigationBarItem(

            selected = currentRoute == Routes.PROFILE_SETTING,

            onClick = {
                onNavigate(Routes.PROFILE_SETTING)
            },

            alwaysShowLabel = true,

            icon = {

                Icon(
                    imageVector =
                        if (currentRoute == Routes.PROFILE_SETTING)
                            Icons.Filled.Settings
                        else
                            Icons.Outlined.Settings,

                    contentDescription = null
                )
            },

            label = {
                Text(
                    text = "Settings",
                    fontSize = 11.sp
                )
            }
        )
    }
}

private fun formatRelativeTime(
    isoTimestamp: String?
): String {

    if (isoTimestamp.isNullOrBlank()) return ""

    return try {

        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.US
        )

        val date =
            inputFormat.parse(isoTimestamp)
                ?: return isoTimestamp

        val now = System.currentTimeMillis()

        val diff = now - date.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        when {

            seconds < 60 -> "Just now"

            minutes < 60 -> "${minutes}m ago"

            hours < 24 -> "${hours}h ago"

            days < 7 -> "${days}d ago"

            else ->
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.US
                ).format(date)
        }

    } catch (e: Exception) {

        isoTimestamp
    }
}