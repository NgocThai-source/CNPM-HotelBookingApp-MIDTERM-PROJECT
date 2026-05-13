package com.hotelbooking.app.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hotelbooking.app.data.repository.BookingItem
import com.hotelbooking.app.ui.theme.AppColors
import java.net.URLEncoder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    navController: NavController,
    isDarkMode: Boolean = false,
    viewModel: MyBookingsViewModel = viewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Tat ca") }
    val filters = listOf("All", "Paid", "Pending")

    val textColor = AppColors.textPrimary(isDarkMode)
    val subTextColor = AppColors.textSecondary(isDarkMode)
    val bgColor = AppColors.background(isDarkMode)

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

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            MyBookingsBottomNav(isDarkMode, "my_bookings") { route ->
                if (route == "home") {
                    navController.navigate(route) {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    navController.navigate(route)
                }
            }
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.fetchBookings() },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.DateRange,
                            contentDescription = null,
                            tint = AppColors.CyanMain,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "My Bookings",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }

                // Search bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search by hotel, booking ID, guest...",
                                color = subTextColor.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = null, tint = subTextColor)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = subTextColor)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.CyanMain,
                            unfocusedBorderColor = AppColors.border(isDarkMode),
                            focusedContainerColor = AppColors.surface(isDarkMode),
                            unfocusedContainerColor = AppColors.surface(isDarkMode),
                            cursorColor = AppColors.CyanMain
                        ),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(color = textColor, fontSize = 15.sp)
                    )
                }

                // Filter chips
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters) { filter ->
                            val isSelected = filter == selectedFilter
                            Surface(
                                onClick = { selectedFilter = filter },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) AppColors.CyanMain else AppColors.surface(isDarkMode),
                                shadowElevation = 1.dp
                            ) {
                                Text(
                                    text = filter,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else subTextColor
                                )
                            }
                        }
                    }
                }

                // Results count
                item {
                    Text(
                        "${filteredBookings.size} booking${if (filteredBookings.size != 1) "s" else ""}",
                        fontSize = 13.sp,
                        color = subTextColor
                    )
                }

                // Error state
                if (error != null && bookings.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    error ?: "Failed to load bookings",
                                    fontSize = 13.sp,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }

                // Empty state
                if (!isLoading && filteredBookings.isEmpty() && error == null) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.EventBusy,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = subTextColor.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No bookings found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                if (searchQuery.isNotBlank() || selectedFilter != "All")
                                    "Try adjusting your search or filter"
                                else "Book your first hotel from the Search tab",
                                fontSize = 14.sp,
                                color = subTextColor
                            )
                        }
                    }
                }

                // Booking cards
                items(filteredBookings, key = { it.bookingId }) { booking ->
                    BookingListCard(
                        booking = booking,
                        isDarkMode = isDarkMode,
                        onSearchClick = {
                            navController.navigate("home") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onPaymentRetry = if (booking.paymentStatus == "pending") {
                            { b ->
                                val encodedTitle = URLEncoder.encode(b.hotelTitle, "UTF-8")
                                val encodedImage = URLEncoder.encode(b.hotelImageUrl, "UTF-8")
                                val paymentRoute = "payment/${b.bookingId}/$encodedTitle/$encodedImage/${b.guestName}/${b.phone}/${b.totalPrice}/${(b.totalPrice * 25000).toLong()}/${b.checkInDate}/${b.checkOutDate}/${b.numberOfNights}"
                                navController.navigate(paymentRoute)
                            }
                        } else null
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun BookingListCard(
    booking: BookingItem,
    isDarkMode: Boolean,
    onSearchClick: () -> Unit,
    onPaymentRetry: ((BookingItem) -> Unit)? = null
) {
    val textColor = AppColors.textPrimary(isDarkMode)
    val subTextColor = AppColors.textSecondary(isDarkMode)
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    val isPaid = booking.paymentStatus == "paid"
    val isPending = booking.paymentStatus == "pending"

    Card(
        modifier = Modifier.fillMaxWidth().then(
            if (isPending && onPaymentRetry != null) {
                Modifier.clickable { onPaymentRetry(booking) }
            } else Modifier
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.surface(isDarkMode)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Hotel image + info header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = booking.hotelImageUrl,
                    contentDescription = booking.hotelTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        booking.hotelTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "ID: ${booking.bookingId}",
                        fontSize = 12.sp,
                        color = AppColors.CyanMain,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "Guest: ${booking.guestName}",
                        fontSize = 12.sp,
                        color = subTextColor
                    )
                    Text(
                        "Phone: ${booking.phone}",
                        fontSize = 11.sp,
                        color = subTextColor.copy(alpha = 0.7f)
                    )
                }
                // Payment status badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPaid) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (isPaid) Icons.Filled.CheckCircle else Icons.Filled.Schedule,
                            contentDescription = null,
                            tint = if (isPaid) Color(0xFF4CAF50) else Color(0xFFFF9800),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (isPaid) "Paid" else "Pending",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPaid) Color(0xFF2E7D32) else Color(0xFFE65100)
                        )
                    }
                }
            }

            HorizontalDivider(color = AppColors.border(isDarkMode).copy(alpha = 0.5f))

            // Details row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Check-in", fontSize = 10.sp, color = subTextColor)
                    Text(booking.checkInDate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                }
                Column {
                    Text("Check-out", fontSize = 10.sp, color = subTextColor)
                    Text(booking.checkOutDate, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                }
                Column {
                    Text("Nights", fontSize = 10.sp, color = subTextColor)
                    Text("${booking.numberOfNights} night${if (booking.numberOfNights > 1) "s" else ""}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total", fontSize = 10.sp, color = subTextColor)
                    Text(
                        "$${currencyFormatter.format(booking.totalPrice)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.CyanMain
                    )
                }
            }

            // Pending retry hint
            if (isPending) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF3E0)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Payment,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Tap card to retry payment",
                            fontSize = 11.sp,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            // Paid timestamp
            if (isPaid && !booking.paidAt.isNullOrBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = subTextColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Paid at: ${formatTimestamp(booking.paidAt)}",
                            fontSize = 11.sp,
                            color = subTextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyBookingsBottomNav(
    isDarkMode: Boolean,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = AppColors.surface(isDarkMode),
        tonalElevation = if (isDarkMode) 0.dp else 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = null) },
            label = { Text("Search", fontSize = 11.sp) },
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AppColors.CyanMain,
                selectedTextColor = AppColors.CyanMain,
                indicatorColor = AppColors.CyanMain.copy(alpha = 0.12f),
                unselectedIconColor = AppColors.textTertiary(isDarkMode),
                unselectedTextColor = AppColors.textTertiary(isDarkMode)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
            label = { Text("Bookings", fontSize = 11.sp) },
            selected = currentRoute == "my_bookings",
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AppColors.CyanMain,
                selectedTextColor = AppColors.CyanMain,
                indicatorColor = AppColors.CyanMain.copy(alpha = 0.12f),
                unselectedIconColor = AppColors.textTertiary(isDarkMode),
                unselectedTextColor = AppColors.textTertiary(isDarkMode)
            )
        )
        NavigationBarItem(
            icon = {
                Box {
                    Icon(Icons.Filled.Notifications, contentDescription = null)
                    if (com.hotelbooking.app.ui.screens.notification.NotificationViewModel.sharedUnreadCount.collectAsState().value > 0) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .background(Color(0xFFF44336), CircleShape)
                        )
                    }
                }
            },
            label = { Text("Notification", fontSize = 11.sp) },
            selected = false,
            onClick = { onNavigate("notifications") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = AppColors.textTertiary(isDarkMode),
                unselectedTextColor = AppColors.textTertiary(isDarkMode)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
            label = { Text("Settings", fontSize = 11.sp) },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = AppColors.textTertiary(isDarkMode),
                unselectedTextColor = AppColors.textTertiary(isDarkMode)
            )
        )
    }
}

private fun formatTimestamp(isoTimestamp: String?): String {
    if (isoTimestamp.isNullOrBlank()) return ""
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
        val date = inputFormat.parse(isoTimestamp)
        date?.let { outputFormat.format(it) } ?: isoTimestamp
    } catch (e: Exception) {
        isoTimestamp
    }
}
