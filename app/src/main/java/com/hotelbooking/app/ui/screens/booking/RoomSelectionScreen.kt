package com.hotelbooking.app.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hotelbooking.app.data.model.Room
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import java.text.NumberFormat
import java.util.Locale

private val roomAmenityIconMap = mapOf(
    "wifi"       to (Icons.Filled.Wifi          to "WiFi"),
    "tv"         to (Icons.Filled.Tv            to "TV"),
    "ac"         to (Icons.Filled.AcUnit        to "AC"),
    "pool"       to (Icons.Filled.Pool          to "Pool"),
    "gym"        to (Icons.Filled.FitnessCenter to "Gym"),
    "parking"    to (Icons.Filled.LocalParking  to "Parking"),
    "restaurant" to (Icons.Filled.LocalDining   to "Restaurant"),
    "spa"        to (Icons.Filled.Spa           to "Spa"),
    "bar"        to (Icons.Filled.LocalBar      to "Bar"),
    "beach"      to (Icons.Filled.BeachAccess   to "Beach"),
)

@Composable
fun RoomSelectionScreen(
    navController: NavController,
    hotelId: String,
    hotelTitle: String,
    hotelImageUrl: String,
    exchangeRate: Double,
    isDarkMode: Boolean = false,
    viewModel: RoomSelectionViewModel = viewModel()
) {
    val rooms by viewModel.rooms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(hotelId) { viewModel.fetchRooms(hotelId) }

    val screenBg = if (isDarkMode)
        Brush.verticalGradient(listOf(AppColors.DarkBackground, Color(0xFF0D1520), AppColors.DarkBackground))
    else
        Brush.verticalGradient(listOf(AppColors.CreamLight, AppColors.CreamSurface, Color(0xFFF2EDE5)))

    val textColor = AppColors.textPrimary(isDarkMode)
    val subTextColor = AppColors.textSecondary(isDarkMode)
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    Box(modifier = Modifier.fillMaxSize().background(screenBg)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid))
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Back button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { navController.popBackStack() }
                        .align(Alignment.CenterStart),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                // Title
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose a Room",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = hotelTitle,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // ── Content ────────────────────────────────────────────
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AppColors.SkyBrand, strokeWidth = 3.dp, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(16.dp))
                            Text("Loading rooms...", color = subTextColor, fontSize = 14.sp)
                        }
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                            Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("Failed to load rooms", fontWeight = FontWeight.SemiBold, color = textColor, fontSize = 16.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(error ?: "", color = subTextColor, fontSize = 13.sp)
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Section header
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(22.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(AppColors.NavyDeep)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = if (rooms.isEmpty()) "No Rooms Available" else "${rooms.size} Room${if (rooms.size != 1) "s" else ""} Available",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = textColor
                            )
                        }

                        if (rooms.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.MeetingRoom, contentDescription = null, tint = subTextColor.copy(alpha = 0.4f), modifier = Modifier.size(56.dp))
                                    Spacer(Modifier.height(12.dp))
                                    Text("No rooms have been added yet", color = subTextColor, fontSize = 14.sp)
                                }
                            }
                        } else {
                            rooms.forEach { room ->
                                RoomCard(
                                    room = room,
                                    exchangeRate = exchangeRate,
                                    currencyFormatter = currencyFormatter,
                                    isDarkMode = isDarkMode,
                                    onSelect = {
                                        val route = Routes.bookingRoute(
                                            hotelId = hotelId,
                                            hotelTitle = hotelTitle,
                                            hotelPrice = room.pricePerNight,
                                            hotelImageUrl = hotelImageUrl,
                                            checkInAvailable = room.checkInDate ?: "2026-01-01",
                                            checkOutAvailable = room.checkOutDate ?: "2026-12-31",
                                            exchangeRate = exchangeRate,
                                            roomId = room.id,
                                            roomType = room.roomName ?: room.type.ifEmpty { "Standard" }
                                        )
                                        navController.navigate(route)
                                    }
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomCard(
    room: Room,
    exchangeRate: Double,
    currencyFormatter: NumberFormat,
    isDarkMode: Boolean,
    onSelect: () -> Unit
) {
    val textColor = AppColors.textPrimary(isDarkMode)
    val subTextColor = AppColors.textSecondary(isDarkMode)
    val surfaceColor = AppColors.surface(isDarkMode)

    val displayName = room.roomName?.takeIf { it.isNotBlank() }
        ?: room.type.takeIf { it.isNotBlank() }
        ?: "Standard Room"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp), ambientColor = AppColors.NavyDeep.copy(alpha = 0.12f), spotColor = AppColors.NavyDeep.copy(alpha = 0.18f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor)
    ) {
        Column {
            // Room image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val firstImage = room.images?.firstOrNull()
                if (!firstImage.isNullOrBlank()) {
                    AsyncImage(
                        model = firstImage,
                        contentDescription = displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Hotel, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(56.dp))
                    }
                }

                // Price badge (bottom-left glass)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColors.NavyDeep.copy(alpha = 0.85f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "$${currencyFormatter.format(room.pricePerNight)}/night",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }

                // Availability badge (top-right)
                if (room.availableQuantity > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.SkyBrand.copy(alpha = 0.9f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${room.availableQuantity} left",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Room info
            Column(modifier = Modifier.padding(16.dp)) {
                // Room name + capacity row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = textColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!room.roomType.isNullOrBlank() && room.roomType != displayName) {
                            Text(
                                text = room.roomType,
                                fontSize = 12.sp,
                                color = AppColors.SkyBrand,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    // VND price
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${currencyFormatter.format(room.pricePerNight)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = AppColors.NavyDeep
                        )
                        Text(
                            text = "per night",
                            fontSize = 11.sp,
                            color = subTextColor
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Capacity & quantity row
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RoomInfoChip(icon = Icons.Filled.Person, label = "${room.capacity} guest${if (room.capacity != 1) "s" else ""}")
                    if (room.totalQuantity > 0) {
                        RoomInfoChip(icon = Icons.Filled.MeetingRoom, label = "${room.totalQuantity} room${if (room.totalQuantity != 1) "s" else ""}")
                    }
                }

                // Description
                if (!room.description.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = room.description,
                        fontSize = 13.sp,
                        color = subTextColor,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 19.sp
                    )
                }

                // Amenity chips
                val amenities = room.amenities?.filter { it.isNotBlank() } ?: emptyList()
                if (amenities.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    RoomAmenityChips(amenities = amenities)
                }

                Spacer(Modifier.height(14.dp))

                // VND price hint
                val vndTotal = (room.pricePerNight * exchangeRate).toLong()
                val vndFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }
                Text(
                    text = "≈ ${vndFormatter.format(vndTotal)} VND / night",
                    fontSize = 12.sp,
                    color = subTextColor,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(14.dp))

                // Select button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(14.dp),
                            ambientColor = AppColors.NavyDeep.copy(alpha = 0.25f),
                            spotColor = AppColors.NavyDeep.copy(alpha = 0.35f)
                        )
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid)))
                        .clickable { onSelect() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Select This Room",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomInfoChip(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = AppColors.SkyBrand, modifier = Modifier.size(15.dp))
        Text(label, fontSize = 13.sp, color = AppColors.textSecondary(false), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun RoomAmenityChips(amenities: List<String>) {
    val displayed = amenities.take(5)
    val remaining = amenities.size - displayed.size

    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
        displayed.forEach { amenity ->
            val key = amenity.lowercase().trim()
            val (icon, label) = roomAmenityIconMap[key] ?: (Icons.Filled.Star to amenity.replaceFirstChar { it.uppercase() })
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColors.SkyBrand.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(icon, contentDescription = null, tint = AppColors.SkyBrand, modifier = Modifier.size(12.dp))
                    Text(label, fontSize = 11.sp, color = AppColors.SkyBrand, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        if (remaining > 0) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColors.NavyDeep.copy(alpha = 0.08f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("+$remaining", fontSize = 11.sp, color = AppColors.NavyDeep, fontWeight = FontWeight.Bold)
            }
        }
    }
}
