package com.hotelbooking.app.ui.screens.notification

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hotelbooking.app.data.model.Notification
import com.hotelbooking.app.ui.components.AppBottomNavBar
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

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        TokenManager.getUserId()?.let { viewModel.setUserId(it) }
    }

    val hasUnread = unreadCount > 0
    val groupedNotifications = remember(notifications) { groupNotificationsByDate(notifications) }

    val screenBackground = if (isDarkMode) {
        Brush.verticalGradient(
            listOf(AppColors.DarkBackground, Color(0xFF0D1520), AppColors.DarkBackground)
        )
    } else {
        Brush.verticalGradient(
            listOf(AppColors.CreamLight, AppColors.CreamSurface, Color(0xFFF2EDE5))
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            AppBottomNavBar(
                isDarkMode = isDarkMode,
                currentRoute = currentRoute ?: Routes.NOTIFICATIONS,
                onNavigate = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackground)
        ) {
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.fetchNotifications() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // ── GRADIENT HEADER ──────────────────────────────────
                item {
                    NotificationsHeader(
                        unreadCount = unreadCount,
                        hasUnread = hasUnread,
                        isDarkMode = isDarkMode,
                        onMarkAllRead = { viewModel.markAllAsRead() }
                    )
                }

                // ── ERROR ────────────────────────────────────────────
                if (error != null && notifications.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(AppColors.Error.copy(alpha = 0.08f))
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.ErrorOutline,
                                    contentDescription = null,
                                    tint = AppColors.Error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = error ?: "Failed to load notifications",
                                    fontSize = 13.sp,
                                    color = AppColors.Error
                                )
                            }
                        }
                    }
                }

                // ── EMPTY STATE ──────────────────────────────────────
                if (!isLoading && notifications.isEmpty() && error == null) {
                    item {
                        NotificationsEmptyState(isDarkMode = isDarkMode)
                    }
                }

                // ── GROUPED NOTIFICATION LIST ────────────────────────
                groupedNotifications.forEach { (groupTitle, groupItems) ->
                    item(key = "group_$groupTitle") {
                        Text(
                            text = groupTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textTertiary(isDarkMode),
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(
                                start = 20.dp, end = 20.dp,
                                top = 16.dp, bottom = 6.dp
                            )
                        )
                    }

                    items(groupItems, key = { it.id }) { notification ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
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
                    }
                }
            }
        }
        } // Box gradient background
    }
}

// ════════════════════════════════════════════════════════════════
// GRADIENT HEADER
// ════════════════════════════════════════════════════════════════
@Composable
private fun NotificationsHeader(
    unreadCount: Int,
    hasUnread: Boolean,
    isDarkMode: Boolean,
    onMarkAllRead: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, delayMillis = 80),
        label = "header_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp)
            .graphicsLayer { this.alpha = alpha }
    ) {
        // Navy gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid))
                )
        )

        // Decorative glow
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-20).dp)
                .background(
                    Brush.radialGradient(
                        listOf(AppColors.SkyBrand.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = "Notifications",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (hasUnread) "$unreadCount unread" else "All caught up",
                            fontSize = 12.sp,
                            color = if (hasUnread) AppColors.GoldPrimary else AppColors.SkyBrand.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Unread badge
                    if (hasUnread) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(AppColors.GoldPrimary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Mark all read
                    if (hasUnread) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                                .clickable { onMarkAllRead() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Mark all",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Bottom fade — blends into cream background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            if (isDarkMode) AppColors.DarkBackground else AppColors.CreamSurface
                        )
                    )
                )
        )
    }
}

// ════════════════════════════════════════════════════════════════
// NOTIFICATION CARD
// ════════════════════════════════════════════════════════════════
@Composable
private fun NotificationCard(
    notification: Notification,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val (icon, iconColor, iconBg) = when (notification.type) {
        "BOOKING_CONFIRMED" -> Triple(
            Icons.Filled.CheckCircle,
            AppColors.SkyBrand,
            AppColors.SkyBrand.copy(alpha = 0.12f)
        )
        "ADMIN_MESSAGE" -> Triple(
            Icons.Filled.Campaign,
            AppColors.GoldPrimary,
            AppColors.GoldPrimary.copy(alpha = 0.12f)
        )
        else -> Triple(
            Icons.Filled.Notifications,
            AppColors.SkyBrand,
            AppColors.SkyBrand.copy(alpha = 0.1f)
        )
    }

    val cardBg = when {
        !notification.is_read && isDarkMode -> AppColors.NavySurface.copy(alpha = 0.6f)
        !notification.is_read -> AppColors.SkyBrand.copy(alpha = 0.04f)
        isDarkMode -> AppColors.DarkCard
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (notification.is_read) 2.dp else 6.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = AppColors.NavyDeep.copy(alpha = 0.06f),
                spotColor = AppColors.NavyDeep.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(cardBg)
            .clickable(onClick = onClick)
    ) {
        // Left accent bar for unread
        if (!notification.is_read) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(80.dp)
                    .align(Alignment.CenterStart)
                    .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                    .background(AppColors.SkyBrand)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = if (!notification.is_read) 16.dp else 14.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 14.sp,
                        fontWeight = if (notification.is_read) FontWeight.Medium else FontWeight.Bold,
                        color = AppColors.textPrimary(isDarkMode),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (!notification.is_read) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(AppColors.SkyBrand)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = notification.body,
                    fontSize = 13.sp,
                    color = AppColors.textSecondary(isDarkMode),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = formatRelativeTime(notification.created_at),
                    fontSize = 11.sp,
                    color = AppColors.textTertiary(isDarkMode)
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// EMPTY STATE
// ════════════════════════════════════════════════════════════════
@Composable
private fun NotificationsEmptyState(isDarkMode: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            AppColors.SkyBrand.copy(alpha = 0.12f),
                            AppColors.NavyMid.copy(alpha = 0.06f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.NotificationsNone,
                contentDescription = null,
                tint = AppColors.SkyBrand.copy(alpha = 0.7f),
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "All quiet here",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary(isDarkMode)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "You'll receive booking confirmations\nand messages from us here.",
            fontSize = 14.sp,
            color = AppColors.textSecondary(isDarkMode),
            lineHeight = 20.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// ════════════════════════════════════════════════════════════════
// HELPERS
// ════════════════════════════════════════════════════════════════
private fun groupNotificationsByDate(
    notifications: List<Notification>
): List<Pair<String, List<Notification>>> {
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(System.currentTimeMillis())
    val todayItems = notifications.filter { it.created_at.startsWith(todayStr) }
    val earlierItems = notifications.filter { !it.created_at.startsWith(todayStr) }

    val result = mutableListOf<Pair<String, List<Notification>>>()
    if (todayItems.isNotEmpty()) result.add("TODAY" to todayItems)
    if (earlierItems.isNotEmpty()) result.add("EARLIER" to earlierItems)
    return result
}

private fun formatRelativeTime(isoTimestamp: String?): String {
    if (isoTimestamp.isNullOrBlank()) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val date = inputFormat.parse(isoTimestamp) ?: return isoTimestamp
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
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.US).format(date)
        }
    } catch (e: Exception) {
        isoTimestamp
    }
}
