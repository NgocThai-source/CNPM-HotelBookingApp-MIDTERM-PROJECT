package com.hotelbooking.app.ui.screens.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.hotelbooking.app.data.repository.BookingItem
import com.hotelbooking.app.ui.components.BottomNavBar
import com.hotelbooking.app.ui.components.BottomNavItem
import com.hotelbooking.app.ui.components.CyanMain
import com.hotelbooking.app.ui.components.CyanLight
import java.text.NumberFormat
import java.util.Locale

// ── Đã xóa ONGOING và CANCELLED để dọn cảnh báo (Warning) ──
enum class BookingStatus(val label: String, val color: Color, val bgColor: Color, val icon: ImageVector) {
    UPCOMING("Upcoming", Color(0xFF2196F3), Color(0xFFE3F2FD), Icons.Filled.Schedule),
    COMPLETED("Completed", Color(0xFF4CAF50), Color(0xFFE8F5E9), Icons.Filled.CheckCircle)
}

fun getUiStatusFromPayment(paymentStatus: String?): BookingStatus {
    return if (paymentStatus == "paid") BookingStatus.COMPLETED else BookingStatus.UPCOMING
}

@Composable
fun BookingScreen(
    userId: String,
    onNavigate: (String) -> Unit = {},
    viewModel: MyBookingsViewModel = viewModel()
) {
    val bookings by viewModel.bookings.collectAsState()

    LaunchedEffect(userId) {
        viewModel.fetchBookings(userId) // LƯU Ý: Phải sửa MyBookingsViewModel mới hết báo đỏ dòng này
    }

    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("All", "Upcoming", "Completed")

    val filteredBookings = remember(selectedFilter, searchQuery, bookings) {
        bookings.filter { booking ->
            val uiStatus = getUiStatusFromPayment(booking.paymentStatus)

            val matchFilter = selectedFilter == "All" || uiStatus.label == selectedFilter
            val matchSearch = searchQuery.isEmpty() ||
                    booking.hotelTitle.contains(searchQuery, ignoreCase = true) ||
                    booking.bookingId.contains(searchQuery, ignoreCase = true)

            matchFilter && matchSearch
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = BottomNavItem.BOOKINGS.route,
                onItemClick = { item -> onNavigate(item.route) }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item { BookingTopBar() }

            item {
                BookingSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            item { BookingStatsRow(bookings) }

            item {
                BookingCategoryChips(
                    currentFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    filters = filters
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "My Bookings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        "${filteredBookings.size} total",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanMain
                    )
                }
            }

            if (filteredBookings.isEmpty()) {
                item { EmptyBookingState(selectedFilter) }
            } else {
                items(filteredBookings, key = { it.bookingId }) { booking ->
                    BookingCard(booking = booking)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun BookingTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // Đã sửa cảnh báo Icons.Filled.EventNote thành AutoMirrored
            Icon(Icons.AutoMirrored.Filled.EventNote, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Text("My Reservations", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        IconButton(
            onClick = { },
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.Gray)
        }
    }
}

@Composable
private fun BookingSearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search by hotel or booking ID...", color = Color.Gray, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.Gray) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = Color.Gray)
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = CyanMain,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
private fun BookingStatsRow(bookings: List<BookingItem>) {
    val upcomingCount = bookings.count { getUiStatusFromPayment(it.paymentStatus) == BookingStatus.UPCOMING }
    val completedCount = bookings.count { getUiStatusFromPayment(it.paymentStatus) == BookingStatus.COMPLETED }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Schedule,
            value = "$upcomingCount",
            label = "Pending",
            color = Color(0xFF2196F3)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.CheckCircle,
            value = "$completedCount",
            label = "Paid",
            color = Color(0xFF4CAF50)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Hotel,
            value = "${bookings.size}",
            label = "Total",
            color = Color(0xFF9E9E9E)
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun BookingCategoryChips(currentFilter: String, onFilterSelected: (String) -> Unit, filters: List<String>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters) { filter ->
            val isSelected = filter == currentFilter
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) CyanMain else Color.White)
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    filter,
                    color = if (isSelected) Color.White else Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun BookingCard(booking: BookingItem) {
    val uiStatus = getUiStatusFromPayment(booking.paymentStatus)
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = booking.hotelImageUrl,
                    contentDescription = booking.hotelTitle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(10.dp),
                    color = uiStatus.bgColor.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(uiStatus.icon, contentDescription = null, tint = uiStatus.color, modifier = Modifier.size(14.dp))
                        Text(if (booking.paymentStatus == "paid") "Paid" else "Pending", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = uiStatus.color)
                    }
                }

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("4.5", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        "#${booking.bookingId}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        booking.hotelTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("$${currencyFormatter.format(booking.totalPrice)}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = CyanMain)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Vietnam", fontSize = 13.sp, color = Color.Gray)
                    }
                    Text("TOTAL PRICE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BookingDetailItem(Icons.Outlined.CalendarToday, "Check-in", booking.checkInDate, Modifier.weight(1f))
                    BookingDetailItem(Icons.Outlined.EventAvailable, "Check-out", booking.checkOutDate, Modifier.weight(1f))
                    BookingDetailItem(Icons.Outlined.Groups, "Guests", "${booking.guestCount} Pax", Modifier.weight(0.8f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    when (uiStatus) {
                        BookingStatus.UPCOMING -> {
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                // Đã sửa cảnh báo deprecated BorderStroke
                                border = BorderStroke(1.dp, Color(0xFFF44336)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336))
                            ) {
                                Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Button(
                                onClick = { },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanMain)
                            ) {
                                Text("Pay Now", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                        BookingStatus.COMPLETED -> {
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, CyanMain),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanMain)
                            ) {
                                Text("Review", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Button(
                                onClick = { },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanMain)
                            ) {
                                Text("Book Again", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookingDetailItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8F9FA)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = CyanMain, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun EmptyBookingState(filter: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(CyanLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.EventBusy, contentDescription = null, tint = CyanMain, modifier = Modifier.size(50.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("No $filter Bookings", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "It looks like you don't have any bookings here.\nStart exploring and book your next stay!",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { },
            modifier = Modifier.height(50.dp).padding(horizontal = 24.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CyanMain)
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Find Hotels", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
        }
    }
}