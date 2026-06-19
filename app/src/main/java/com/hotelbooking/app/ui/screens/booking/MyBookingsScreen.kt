package com.hotelbooking.app.ui.screens.booking

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.hotelbooking.app.data.repository.BookingItem
import com.hotelbooking.app.ui.components.AppBottomNavBar
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    navController: NavController,
    userId: String,
    isDarkMode: Boolean = false,
    viewModel: MyBookingsViewModel = viewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBookings(userId)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "Paid", "Pending")

    val filteredBookings = remember(bookings, searchQuery, selectedFilter) {
        bookings.filter { booking ->
            val matchSearch = searchQuery.isBlank() ||
                    booking.hotelTitle.contains(searchQuery, ignoreCase = true) ||
                    booking.bookingId.contains(searchQuery, ignoreCase = true) ||
                    booking.guestName.contains(searchQuery, ignoreCase = true)
            val matchFilter = when (selectedFilter) {
                "Paid" -> booking.paymentStatus == "paid"
                "Pending" -> booking.paymentStatus == "pending"
                else -> true
            }
            matchSearch && matchFilter
        }
    }

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
                currentRoute = currentRoute ?: Routes.MY_BOOKINGS,
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
            onRefresh = { viewModel.fetchBookings(userId) },
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
                    BookingsHeader(
                        bookingCount = filteredBookings.size,
                        isDarkMode = isDarkMode
                    )
                }

                // ── SEARCH BAR ───────────────────────────────────────
                item {
                    BookingsSearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        isDarkMode = isDarkMode
                    )
                }

                // ── FILTER CHIPS ─────────────────────────────────────
                item {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filters) { filter ->
                            val isSelected = filter == selectedFilter
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected)
                                            Brush.horizontalGradient(
                                                listOf(AppColors.GoldPrimary, AppColors.GoldDark)
                                            )
                                        else
                                            Brush.horizontalGradient(
                                                listOf(
                                                    AppColors.border(isDarkMode),
                                                    AppColors.border(isDarkMode)
                                                )
                                            )
                                    )
                                    .clickable { selectedFilter = filter }
                                    .padding(horizontal = 18.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else AppColors.textSecondary(isDarkMode)
                                )
                            }
                        }
                    }
                }

                // ── BOOKING CARDS ────────────────────────────────────
                if (filteredBookings.isNotEmpty()) {
                    items(filteredBookings, key = { it.bookingId }) { booking ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            BookingListCard(
                                booking = booking,
                                isDarkMode = isDarkMode,
                                onPaymentRetry = if (booking.paymentStatus == "pending") {
                                    { b ->
                                        val encodedTitle = URLEncoder.encode(b.hotelTitle, "UTF-8")
                                        val encodedImage = URLEncoder.encode(b.hotelImageUrl, "UTF-8")
                                        val paymentRoute =
                                            "payment/${b.bookingId}/$encodedTitle/$encodedImage/${b.guestName}/${b.phone}/${b.totalPrice}/${(b.totalPrice * 25000).toLong()}/${b.checkInDate}/${b.checkOutDate}/${b.numberOfNights}"
                                        navController.navigate(paymentRoute)
                                    }
                                } else null
                            )
                        }
                    }
                }

                // ── EMPTY STATE ──────────────────────────────────────
                if (filteredBookings.isEmpty() && !isLoading) {
                    item {
                        BookingsEmptyState(isDarkMode = isDarkMode)
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
private fun BookingsHeader(bookingCount: Int, isDarkMode: Boolean) {
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
            .height(215.dp)
            .graphicsLayer { this.alpha = alpha }
    ) {
        // Navy gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(
                    Brush.verticalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid))
                )
        )

        // Decorative radial glow top-right
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .background(
                    Brush.radialGradient(
                        listOf(AppColors.GoldPrimary.copy(alpha = 0.15f), Color.Transparent)
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
                        text = "Your Trips",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "$bookingCount booking${if (bookingCount != 1) "s" else ""}",
                        fontSize = 12.sp,
                        color = AppColors.SkyBrand.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                        tint = AppColors.GoldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
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
// SEARCH BAR
// ════════════════════════════════════════════════════════════════
@Composable
private fun BookingsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isDarkMode: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = AppColors.NavyDeep.copy(alpha = 0.15f),
                spotColor = AppColors.NavyDeep.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDarkMode) AppColors.DarkCard else Color.White)
            .padding(horizontal = 14.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
                tint = AppColors.SkyBrand,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 14.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    color = AppColors.textPrimary(isDarkMode),
                    fontWeight = FontWeight.Normal
                ),
                singleLine = true,
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text(
                            "Search by hotel, guest or ID…",
                            fontSize = 14.sp,
                            color = AppColors.textTertiary(isDarkMode)
                        )
                    }
                    inner()
                }
            )
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Clear",
                        tint = AppColors.textTertiary(isDarkMode),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// BOOKING CARD
// ════════════════════════════════════════════════════════════════
@Composable
private fun BookingListCard(
    booking: BookingItem,
    isDarkMode: Boolean,
    onPaymentRetry: ((BookingItem) -> Unit)? = null
) {
    val isPaid = booking.paymentStatus == "paid"
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (!isPaid && onPaymentRetry != null)
                    Modifier.clickable { onPaymentRetry(booking) }
                else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkMode) 0.dp else 6.dp
        )
    ) {
        Column {
            // ── Top section: image + info ─────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = booking.hotelImageUrl,
                    contentDescription = booking.hotelTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(14.dp))
                )

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Hotel name + status badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = booking.hotelTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.textPrimary(isDarkMode),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        StatusBadge(isPaid = isPaid)
                    }

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "#${booking.bookingId.take(8).uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.SkyBrand
                    )

                    Spacer(Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = AppColors.textTertiary(isDarkMode),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = booking.guestName,
                            fontSize = 12.sp,
                            color = AppColors.textSecondary(isDarkMode),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // ── Divider ──────────────────────────────────────────
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 14.dp),
                thickness = 0.5.dp,
                color = AppColors.border(isDarkMode).copy(alpha = 0.5f)
            )

            // ── Bottom section: dates + price ─────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date timeline
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CHECK IN",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textTertiary(isDarkMode),
                            letterSpacing = 0.8.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = booking.checkInDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.textPrimary(isDarkMode)
                        )
                    }

                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.SkyBrand.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${booking.numberOfNights}n",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.SkyBrand
                        )
                    }
                    Spacer(Modifier.width(8.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CHECK OUT",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textTertiary(isDarkMode),
                            letterSpacing = 0.8.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = booking.checkOutDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.textPrimary(isDarkMode)
                        )
                    }
                }

                // Price
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "TOTAL",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textTertiary(isDarkMode),
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "$${currencyFormatter.format(booking.totalPrice)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDarkMode) Color.White else AppColors.NavyDeep
                    )
                }
            }

            // ── Pending CTA ───────────────────────────────────────
            if (!isPaid) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(AppColors.GoldDark, AppColors.GoldPrimary)
                            )
                        )
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Filled.Payment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Tap to complete payment",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// STATUS BADGE
// ════════════════════════════════════════════════════════════════
@Composable
private fun StatusBadge(isPaid: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isPaid)
                    AppColors.SkyBrand.copy(alpha = 0.15f)
                else
                    AppColors.GoldPrimary.copy(alpha = 0.15f)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (isPaid) "Paid" else "Pending",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPaid) AppColors.SkyDark else AppColors.GoldDark
        )
    }
}

// ════════════════════════════════════════════════════════════════
// EMPTY STATE
// ════════════════════════════════════════════════════════════════
@Composable
private fun BookingsEmptyState(isDarkMode: Boolean) {
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
                            AppColors.SkyBrand.copy(alpha = 0.15f),
                            AppColors.GoldPrimary.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.DateRange,
                contentDescription = null,
                tint = AppColors.SkyBrand,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "No trips yet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary(isDarkMode)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Your bookings will appear here.\nStart exploring to find your perfect stay.",
            fontSize = 14.sp,
            color = AppColors.textSecondary(isDarkMode),
            lineHeight = 20.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
