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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hotelbooking.app.ui.components.BottomNavBar
import com.hotelbooking.app.ui.components.BottomNavItem
import com.hotelbooking.app.ui.components.CyanMain
import com.hotelbooking.app.ui.components.CyanLight

// ── Booking Status Enum ──
enum class BookingStatus(val label: String, val color: Color, val bgColor: Color, val icon: ImageVector) {
    UPCOMING("Upcoming", Color(0xFF2196F3), Color(0xFFE3F2FD), Icons.Filled.Schedule),
    ONGOING("Ongoing", Color(0xFF4CAF50), Color(0xFFE8F5E9), Icons.Filled.PlayCircle),
    COMPLETED("Completed", Color(0xFF9E9E9E), Color(0xFFF5F5F5), Icons.Filled.CheckCircle),
    CANCELLED("Cancelled", Color(0xFFF44336), Color(0xFFFFEBEE), Icons.Filled.Cancel)
}

// ── Mock Booking Data ──
data class BookingItem(
    val id: String,
    val hotelName: String,
    val location: String,
    val imageUrl: String,
    val checkIn: String,
    val checkOut: String,
    val guests: Int,
    val rooms: Int,
    val totalPrice: String,
    val status: BookingStatus,
    val rating: String,
    val roomType: String
)

private val mockBookings = listOf(
    BookingItem("BK001", "The Azure Grand Resort", "Maldives", "https://images.pexels.com/photos/189296/pexels-photo-189296.jpeg", "May 15, 2026", "May 18, 2026", 2, 1, "$1,350", BookingStatus.UPCOMING, "4.9", "Deluxe Ocean Suite"),
    BookingItem("BK002", "Emerald Isle Resort", "Bora Bora", "https://images.pexels.com/photos/1001965/pexels-photo-1001965.jpeg", "May 10, 2026", "May 12, 2026", 2, 1, "$1,240", BookingStatus.ONGOING, "5.0", "Overwater Bungalow"),
    BookingItem("BK003", "Lumiere Heritage Hotel", "Paris", "https://images.pexels.com/photos/258154/pexels-photo-258154.jpeg", "Apr 20, 2026", "Apr 23, 2026", 1, 1, "$960", BookingStatus.COMPLETED, "4.7", "Classic Double Room"),
    BookingItem("BK004", "The Ritz-Carlton Sky", "Tokyo", "https://images.pexels.com/photos/164595/pexels-photo-164595.jpeg", "Apr 5, 2026", "Apr 8, 2026", 2, 1, "$1,800", BookingStatus.COMPLETED, "4.9", "Sky Premium Suite"),
    BookingItem("BK005", "Golden Coast Palace", "Miami", "https://images.pexels.com/photos/2034335/pexels-photo-2034335.jpeg", "Mar 28, 2026", "Mar 30, 2026", 3, 2, "$1,100", BookingStatus.CANCELLED, "4.6", "Beachfront Villa"),
    BookingItem("BK006", "Silver Peak Mountain Inn", "Colorado", "https://images.pexels.com/photos/1457842/pexels-photo-1457842.jpeg", "Jun 1, 2026", "Jun 5, 2026", 4, 2, "$1,120", BookingStatus.UPCOMING, "4.8", "Mountain View Cabin")
)

// ── Main BookingScreen ──
@Composable
fun BookingScreen(onNavigate: (String) -> Unit = {}) {
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("All", "Upcoming", "Ongoing", "Completed", "Cancelled")

    val filteredBookings = remember(selectedFilter, searchQuery) {
        mockBookings.filter { 
            (selectedFilter == "All" || it.status.label == selectedFilter) &&
            (searchQuery.isEmpty() || it.hotelName.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true))
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

            // ── Top Bar ──
            item { BookingTopBar() }

            // ── Search Bar ──
            item {
                BookingSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }

            // ── Stats Summary Cards ──
            item { BookingStatsRow() }

            // ── Filter Chips ──
            item {
                BookingCategoryChips(
                    currentFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    filters = filters
                )
            }

            // ── Section Header ──
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

            // ── Booking Cards or Empty State ──
            if (filteredBookings.isEmpty()) {
                item { EmptyBookingState(selectedFilter) }
            } else {
                items(filteredBookings, key = { it.id }) { booking ->
                    BookingCard(booking = booking)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ── Top Bar ──
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
            Icon(Icons.Filled.EventNote, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(24.dp))
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

// ── Search Bar ──
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

// ── Stats Row ──
@Composable
private fun BookingStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Schedule,
            value = "${mockBookings.count { it.status == BookingStatus.UPCOMING }}",
            label = "Upcoming",
            color = Color(0xFF2196F3)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.PlayCircle,
            value = "${mockBookings.count { it.status == BookingStatus.ONGOING }}",
            label = "Ongoing",
            color = Color(0xFF4CAF50)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.CheckCircle,
            value = "${mockBookings.count { it.status == BookingStatus.COMPLETED }}",
            label = "Done",
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

// ── Category Chips ──
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

// ── Booking Card ──
@Composable
private fun BookingCard(booking: BookingItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Hotel Image with Status Badge
            Box {
                AsyncImage(
                    model = booking.imageUrl,
                    contentDescription = booking.hotelName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Status Badge (Top-Right)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd),
                    shape = RoundedCornerShape(10.dp),
                    color = booking.status.bgColor.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(booking.status.icon, contentDescription = null, tint = booking.status.color, modifier = Modifier.size(14.dp))
                        Text(booking.status.label, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = booking.status.color)
                    }
                }

                // Rating Badge (Top-Left)
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
                        Text(booking.rating, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                // Booking ID Badge (Bottom-Left)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        "#${booking.id}", 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White
                    )
                }
            }

            // Info Section
            Column(modifier = Modifier.padding(20.dp)) {
                // Hotel Name & Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        booking.hotelName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(booking.totalPrice, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = CyanMain)
                }

                // Location & Room Type
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(booking.location, fontSize = 13.sp, color = Color.Gray)
                    }
                    Text("TOTAL PRICE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dates & Guest Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BookingDetailItem(Icons.Outlined.CalendarToday, "Check-in", booking.checkIn, Modifier.weight(1f))
                    BookingDetailItem(Icons.Outlined.EventAvailable, "Check-out", booking.checkOut, Modifier.weight(1f))
                    BookingDetailItem(Icons.Outlined.Groups, "Guests", "${booking.guests} Pax", Modifier.weight(0.8f))
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    when (booking.status) {
                        BookingStatus.UPCOMING -> {
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.linearGradient(listOf(Color(0xFFF44336), Color(0xFFF44336)))
                                ),
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
                                Text("Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                        BookingStatus.ONGOING -> {
                            Button(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                            ) {
                                Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Contact Support", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                        BookingStatus.COMPLETED -> {
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(12.dp),
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
                        BookingStatus.CANCELLED -> {
                            Button(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanMain)
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Try Rebooking", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Detail Item ──
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

// ── Empty State ──
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
