package com.hotelbooking.app.ui.screens.notification

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hotelbooking.app.data.model.MockNotifications
import com.hotelbooking.app.data.model.Notification
import com.hotelbooking.app.data.model.NotificationType
import com.hotelbooking.app.ui.components.AppBottomNavBar
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import com.hotelbooking.app.util.TokenManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

// ════════════════════════════════════════════════════════════════
// SCREEN
// ════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    isDarkMode: Boolean = false,
    onNavigateToBookings: () -> Unit = {},
    viewModel: NotificationViewModel = viewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var useMockData by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        TokenManager.getUserId()?.let { viewModel.setUserId(it) }
        // Toggle mock data for preview: set to true to see the new UI
        // useMockData = true
    }

    val displayNotifications = remember(useMockData, notifications) {
        if (useMockData) MockNotifications.getAll() else notifications
    }

    val hasUnread = displayNotifications.count { !it.is_read } > 0
    val groupedNotifications = remember(displayNotifications) { groupNotificationsByDate(displayNotifications) }

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
                    // ── GRADIENT HEADER ──────────────────────────────
                    item {
                        NotificationsHeader(
                            unreadCount = displayNotifications.count { !it.is_read },
                            hasUnread = hasUnread,
                            isDarkMode = isDarkMode,
                            onMarkAllRead = { viewModel.markAllAsRead() }
                        )
                    }

                    // ── ERROR ─────────────────────────────────────
                    if (error != null && displayNotifications.isEmpty()) {
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

                    // ── EMPTY STATE ────────────────────────────────
                    if (!isLoading && displayNotifications.isEmpty() && error == null) {
                        item {
                            NotificationsEmptyState(isDarkMode = isDarkMode)
                        }
                    }

                    // ── LOADING SKELETON ───────────────────────────
                    if (isLoading && displayNotifications.isEmpty()) {
                        items(3) { index ->
                            NotificationSkeletonItem(
                                isDarkMode = isDarkMode,
                                delay = index * 100
                            )
                        }
                    }

                    // ── GROUPED NOTIFICATION LIST ──────────────────
                    groupedNotifications.forEach { (groupTitle, groupItems) ->
                        item(key = "group_$groupTitle") {
                            GroupHeader(
                                title = groupTitle,
                                isDarkMode = isDarkMode
                            )
                        }

                        itemsIndexed(
                            groupItems,
                            key = { _, notification -> notification.id }
                        ) { index, notification ->
                            NotificationCard(
                                notification = notification,
                                isDarkMode = isDarkMode,
                                index = index,
                                onClick = {
                                    if (!notification.is_read) {
                                        viewModel.markAsRead(notification.id)
                                    }
                                    if (notification.type == "BOOKING_CONFIRMED") {
                                        onNavigateToBookings()
                                    }
                                },
                                onMarkRead = { viewModel.markAsRead(notification.id) },
                                onDelete = { viewModel.deleteNotification(notification.id) }
                            )
                        }
                    }
                }
            }
        }
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

    val headerAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500, delayMillis = 80),
        label = "header_alpha"
    )

    var markAllPressed by remember { mutableStateOf(false) }
    val markAllScale by animateFloatAsState(
        targetValue = if (markAllPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "markAll_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp)
            .graphicsLayer { alpha = headerAlpha }
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

        // Secondary glow accent
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-20).dp, y = 40.dp)
                .background(
                    Brush.radialGradient(
                        listOf(AppColors.GoldPrimary.copy(alpha = 0.08f), Color.Transparent)
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Unread pill badge
                    if (hasUnread) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(AppColors.GoldPrimary)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Mark all button — outline glass style
                    if (hasUnread) {
                        Box(
                            modifier = Modifier
                                .graphicsLayer { scaleX = markAllScale; scaleY = markAllScale }
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .drawBehind {
                                    drawRect(
                                        color = Color.White.copy(alpha = 0.2f),
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                                    )
                                }
                                .clickable {
                                    markAllPressed = true
                                    onMarkAllRead()
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    Icons.Filled.DoneAll,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
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
        }

        // Bottom fade — blends into content background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
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

    LaunchedEffect(markAllPressed) {
        if (markAllPressed) {
            kotlinx.coroutines.delay(150)
            markAllPressed = false
        }
    }
}

// ════════════════════════════════════════════════════════════════
// GROUP HEADER (EYEBROW)
// ════════════════════════════════════════════════════════════════
@Composable
private fun GroupHeader(
    title: String,
    isDarkMode: Boolean
) {
    val (icon, label) = when (title) {
        "TODAY" -> Icons.Filled.WbSunny to "Today"
        "YESTERDAY" -> Icons.Filled.CalendarToday to "Yesterday"
        else -> Icons.Filled.History to "Earlier"
    }

    Row(
        modifier = Modifier.padding(
            start = 20.dp, end = 20.dp,
            top = 16.dp, bottom = 8.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.textTertiary(isDarkMode),
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = label.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textTertiary(isDarkMode),
            letterSpacing = 1.8.sp
        )
    }
}

// ════════════════════════════════════════════════════════════════
// NOTIFICATION CARD
// ════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    notification: Notification,
    isDarkMode: Boolean,
    index: Int,
    onClick: () -> Unit,
    onMarkRead: () -> Unit,
    onDelete: () -> Unit
) {
    val notificationType = notification.notificationType

    // Staggered entrance animation
    var cardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 60L)
        cardVisible = true
    }

    val cardAlpha by animateFloatAsState(
        targetValue = if (cardVisible) 1f else 0f,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "card_alpha"
    )

    val cardOffsetY by animateFloatAsState(
        targetValue = if (cardVisible) 0f else 16f,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "card_offsetY"
    )

    // Press scale animation
    var isPressed by remember { mutableStateOf(false) }
    val pressScaleX by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "scaleX"
    )
    val pressScaleY by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "scaleY"
    )

    // Card background
    val cardBg = when {
        !notification.is_read && isDarkMode -> AppColors.NavySurface.copy(alpha = 0.55f)
        !notification.is_read -> AppColors.CreamDark.copy(alpha = 0.3f)
        isDarkMode -> AppColors.DarkCard
        else -> Color.White
    }

    // Icon from type
    val icon = when (notificationType) {
        NotificationType.BOOKING_CONFIRMED -> Icons.Filled.CheckCircle
        NotificationType.CHECK_IN_REMINDER -> Icons.Filled.EventAvailable
        NotificationType.FAVORITE_ADDED -> Icons.Filled.Favorite
        NotificationType.PAYMENT_SUCCESS -> Icons.Filled.Payments
        NotificationType.PROMO_OFFER -> Icons.Filled.LocalOffer
        NotificationType.WELCOME -> Icons.Filled.Stars
        NotificationType.SYSTEM_INFO -> Icons.Filled.Info
    }

    // Swipe state
    val swipeThreshold = with(LocalDensity.current) { 80.dp.toPx() }
    var offsetX by remember { mutableStateOf(0f) }
    val swipeProgress = (offsetX / swipeThreshold).coerceIn(-1f, 1f)

    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .graphicsLayer {
                alpha = cardAlpha
                translationY = cardOffsetY
                scaleX = pressScaleX
                scaleY = pressScaleY
            }
    ) {
        // Swipe background actions
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if ((swipeProgress > 0 && !isRtl) || (swipeProgress < 0 && isRtl)) {
                        AppColors.SkyBrand.copy(alpha = 0.12f + swipeProgress * 0.08f)
                    } else {
                        AppColors.Error.copy(alpha = 0.08f + (-swipeProgress) * 0.08f)
                    }
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = if ((swipeProgress > 0 && !isRtl) || (swipeProgress < 0 && isRtl)) {
                    Arrangement.Start
                } else {
                    Arrangement.End
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (swipeProgress.absoluteValue > 0.15f) {
                    if ((swipeProgress > 0 && !isRtl) || (swipeProgress < 0 && isRtl)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.graphicsLayer { alpha = swipeProgress.coerceIn(0f, 1f) }
                        ) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                tint = AppColors.SkyBrand,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Read",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.SkyBrand
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.graphicsLayer { alpha = (-swipeProgress).coerceIn(0f, 1f) }
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = null,
                                tint = AppColors.Error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Delete",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.Error
                            )
                        }
                    }
                }
            }
        }

        // Main card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (notification.is_read) 2.dp else 8.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = notificationType.accentBarColor.copy(alpha = 0.06f),
                    spotColor = notificationType.accentBarColor.copy(alpha = 0.1f)
                )
                .clip(RoundedCornerShape(18.dp))
                .background(cardBg)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX = (offsetX + delta).coerceIn(-swipeThreshold * 1.2f, swipeThreshold * 1.2f)
                    },
                    onDragStopped = {
                        if (swipeProgress.absoluteValue > 0.45f) {
                            if ((swipeProgress > 0 && !isRtl) || (swipeProgress < 0 && isRtl)) {
                                onMarkRead()
                            } else {
                                onDelete()
                            }
                        }
                        offsetX = 0f
                    }
                )
                .clickable(
                    onClick = onClick
                )
        ) {
            // Left accent bar (unread only) — colored by type
            if (!notification.is_read) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(notificationType.accentBarColor)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = if (!notification.is_read) 18.dp else 16.dp,
                        end = 14.dp,
                        top = 14.dp,
                        bottom = 14.dp
                    ),
                verticalAlignment = Alignment.Top
            ) {
                // Icon circle
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (notification.is_read) {
                                notificationType.iconBg.copy(alpha = 0.5f)
                            } else {
                                notificationType.iconBg
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (notification.is_read) {
                            notificationType.iconColor.copy(alpha = 0.6f)
                        } else {
                            notificationType.iconColor
                        },
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
                            // Unread dot with glow
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .shadow(
                                        elevation = 3.dp,
                                        shape = CircleShape,
                                        ambientColor = AppColors.SkyBrand.copy(alpha = 0.4f),
                                        spotColor = AppColors.SkyBrand.copy(alpha = 0.5f)
                                    )
                                    .clip(CircleShape)
                                    .background(AppColors.SkyBrand)
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = notification.body,
                        fontSize = 13.sp,
                        color = AppColors.textSecondary(isDarkMode).copy(
                            alpha = if (notification.is_read) 0.75f else 1f
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = notificationType.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = notificationType.iconColor.copy(
                                alpha = if (notification.is_read) 0.5f else 0.8f
                            )
                        )
                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = AppColors.textTertiary(isDarkMode)
                        )
                        Text(
                            text = formatRelativeTime(notification.created_at),
                            fontSize = 11.sp,
                            color = AppColors.textTertiary(isDarkMode)
                        )
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// NOTIFICATION SKELETON
// ════════════════════════════════════════════════════════════════
@Composable
private fun NotificationSkeletonItem(
    isDarkMode: Boolean,
    delay: Int
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0.3f,
        animationSpec = tween(600),
        label = "skeleton_alpha"
    )

    val shimmerOffset by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = -200f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    val skeletonBg = if (isDarkMode) AppColors.DarkCard else Color(0xFFF0EDE6)
    val shimmerColor = if (isDarkMode) {
        Color.White.copy(alpha = 0.06f)
    } else {
        Color.White.copy(alpha = 0.7f)
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .fillMaxWidth()
            .graphicsLayer { alpha = animatedAlpha }
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = AppColors.NavyDeep.copy(alpha = 0.04f),
                spotColor = AppColors.NavyDeep.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(skeletonBg)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // Icon skeleton
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(skeletonBg, shimmerColor, skeletonBg),
                            start = androidx.compose.ui.geometry.Offset(shimmerOffset, 0f),
                            end = androidx.compose.ui.geometry.Offset(shimmerOffset + 200f, 100f)
                        )
                    )
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Title skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(skeletonBg, shimmerColor, skeletonBg),
                                start = androidx.compose.ui.geometry.Offset(shimmerOffset, 0f),
                                end = androidx.compose.ui.geometry.Offset(shimmerOffset + 200f, 100f)
                            )
                        )
                )

                Spacer(Modifier.height(8.dp))

                // Body skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(skeletonBg, shimmerColor, skeletonBg),
                                start = androidx.compose.ui.geometry.Offset(shimmerOffset, 0f),
                                end = androidx.compose.ui.geometry.Offset(shimmerOffset + 200f, 100f)
                            )
                        )
                )

                Spacer(Modifier.height(5.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.45f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(skeletonBg, shimmerColor, skeletonBg),
                                start = androidx.compose.ui.geometry.Offset(shimmerOffset, 0f),
                                end = androidx.compose.ui.geometry.Offset(shimmerOffset + 200f, 100f)
                            )
                        )
                )

                Spacer(Modifier.height(8.dp))

                // Time skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(skeletonBg, shimmerColor, skeletonBg),
                                start = androidx.compose.ui.geometry.Offset(shimmerOffset, 0f),
                                end = androidx.compose.ui.geometry.Offset(shimmerOffset + 200f, 100f)
                            )
                        )
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
    var ringVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { ringVisible = true }

    val ringAlpha by animateFloatAsState(
        targetValue = if (ringVisible) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200),
        label = "ring_alpha"
    )

    val ringScale by animateFloatAsState(
        targetValue = if (ringVisible) 1f else 0.7f,
        animationSpec = tween(800, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "ring_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Animated outer ring
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        alpha = ringAlpha * 0.15f
                        scaleX = ringScale
                        scaleY = ringScale
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                AppColors.SkyBrand.copy(alpha = 0.3f),
                                AppColors.SkyBrand.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Middle ring
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        alpha = ringAlpha * 0.25f
                        scaleX = ringScale * 0.9f
                        scaleY = ringScale * 0.9f
                    }
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                AppColors.GoldPrimary.copy(alpha = 0.2f),
                                AppColors.GoldPrimary.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Icon container
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(20.dp))
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
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "You're all caught up!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary(isDarkMode)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Booking confirmations, messages,\nand exclusive offers will appear here.",
            fontSize = 14.sp,
            color = AppColors.textSecondary(isDarkMode),
            lineHeight = 20.sp,
            textAlign = TextAlign.Center
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
    val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).run {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        format(cal.time)
    }

    val todayItems = notifications.filter { it.created_at.startsWith(todayStr) }
    val yesterdayItems = notifications.filter { it.created_at.startsWith(yesterdayStr) }
    val earlierItems = notifications.filter {
        !it.created_at.startsWith(todayStr) && !it.created_at.startsWith(yesterdayStr)
    }

    val result = mutableListOf<Pair<String, List<Notification>>>()
    if (todayItems.isNotEmpty()) result.add("TODAY" to todayItems)
    if (yesterdayItems.isNotEmpty()) result.add("YESTERDAY" to yesterdayItems)
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
