package com.hotelbooking.app.ui.screens.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Locale

data class Review(val name: String, val date: String, val content: String, val avatarUrl: String)

fun getDefaultAvatarUrl(userName: String): String {
    val encodedName = java.net.URLEncoder.encode(userName.ifBlank { "User" }, "UTF-8")
    return "https://ui-avatars.com/api/?name=$encodedName&background=random&color=fff&size=128"
}

@Composable
fun HotelDetailScreen(
    navController: NavController,
    hotelId: String,
    isDarkMode: Boolean = false,
    viewModel: com.hotelbooking.app.ui.screens.detail.HotelDetailViewModel
) {
    val context = LocalContext.current
    val detailState = viewModel.detailState
    val exchangeRate by viewModel.exchangeRate.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val hasUserReviewed by viewModel.hasUserReviewed.collectAsState()
    val isSubmittingReview by viewModel.isSubmittingReview.collectAsState()

    LaunchedEffect(hotelId) { viewModel.fetchHotelDetail(hotelId) }

    var userReview by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }

    val textColor = AppColors.textPrimary(isDarkMode)
    val subTextColor = AppColors.textSecondary(isDarkMode)
    val dividerColor = AppColors.border(isDarkMode)

    when (detailState) {
        is HotelDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(AppColors.background(isDarkMode)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppColors.CyanMain, strokeWidth = 3.dp)
            }
        }
        is HotelDetailState.Error -> {
            Box(modifier = Modifier.fillMaxSize().background(AppColors.background(isDarkMode)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Icon(Icons.Filled.ErrorOutline, contentDescription = null, modifier = Modifier.size(56.dp), tint = AppColors.Error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Error: ${detailState.message}", color = AppColors.textSecondary(isDarkMode), fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = { viewModel.fetchHotelDetail(hotelId) },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.CyanMain),
                        shape = RoundedCornerShape(14.dp)
                    ) { Text("Try Again", fontWeight = FontWeight.Bold, color = Color.White) }
                }
            }
        }
        is HotelDetailState.Success -> {
            val hotel = detailState.hotel

            Scaffold(
                bottomBar = {
                    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = AppColors.surface(isDarkMode)) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = "Price per night", color = subTextColor, fontSize = 12.sp)
                                Text(text = "$${hotel.price.toInt()}", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = AppColors.CyanMain)
                            }
                            Button(onClick = {
                                val route = Routes.bookingRoute(
                                    hotelId = hotel.id,
                                    hotelTitle = hotel.title,
                                    hotelPrice = hotel.price,
                                    hotelImageUrl = hotel.imageUrl,
                                    checkInAvailable = hotel.checkInDate ?: "2026-01-01",
                                    checkOutAvailable = hotel.checkOutDate ?: "2026-12-31",
                                    exchangeRate = exchangeRate
                                )
                                navController.navigate(route)
                            },
                                shape = RoundedCornerShape(14.dp), modifier = Modifier.height(52.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                            ) { Text(text = "Book Now", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White) }
                        }
                    }
                }
            ) { paddingValues ->
                Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).background(AppColors.background(isDarkMode))) {
                    // Hero Image
                    Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                        AsyncImage(model = hotel.imageUrl, contentDescription = hotel.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp).align(Alignment.BottomCenter)
                            .background(Brush.verticalGradient(colors = listOf(Color.Transparent, AppColors.background(isDarkMode)))))

                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter), horizontalArrangement = Arrangement.SpaceBetween) {
                            Box(modifier = Modifier.size(44.dp).clip(CircleShape)
                                .background(if (isDarkMode) AppColors.DarkCard.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.9f))
                                .clickable { navController.popBackStack() }, contentAlignment = Alignment.Center
                            ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor, modifier = Modifier.size(22.dp)) }

                            Box(modifier = Modifier.size(44.dp).clip(CircleShape)
                                .background(if (isDarkMode) AppColors.DarkCard.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.9f))
                                .clickable {
                                    isFavorite = !isFavorite
                                    Toast.makeText(context, if (isFavorite) "Saved to Favorites" else "Removed from Favorites", Toast.LENGTH_SHORT).show()
                                }, contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorite", tint = if (isFavorite) AppColors.Error else textColor, modifier = Modifier.size(22.dp))
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Surface(shape = RoundedCornerShape(8.dp), color = AppColors.CyanMain.copy(alpha = 0.12f)) {
                                Text(text = hotel.category, color = AppColors.CyanMain, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = hotel.title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = textColor, lineHeight = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = AppColors.textTertiary(isDarkMode), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = hotel.location, fontSize = 14.sp, color = subTextColor)
                        }

                        if (!hotel.checkInDate.isNullOrEmpty() || !hotel.checkOutDate.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                if (!hotel.checkInDate.isNullOrEmpty()) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = AppColors.CyanMain.copy(alpha = 0.1f)) {
                                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Filled.FlightLand, contentDescription = null, tint = AppColors.CyanMain, modifier = Modifier.size(14.dp))
                                            Text("Check-in: ${hotel.checkInDate}", fontSize = 12.sp, color = AppColors.CyanMain, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                                if (!hotel.checkOutDate.isNullOrEmpty()) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = AppColors.CyanMain.copy(alpha = 0.1f)) {
                                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Filled.FlightTakeoff, contentDescription = null, tint = AppColors.CyanMain, modifier = Modifier.size(14.dp))
                                            Text("Check-out: ${hotel.checkOutDate}", fontSize = 12.sp, color = AppColors.CyanMain, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = dividerColor)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Amenities
                        Text(text = "Top Amenities", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            val amenityIcons = mapOf(
                                "wifi" to (Icons.Filled.Wifi to "Free Wifi"),
                                "pool" to (Icons.Filled.Pool to "Pool"),
                                "gym" to (Icons.Filled.FitnessCenter to "Gym"),
                                "restaurant" to (Icons.Filled.LocalDining to "Restaurant"),
                                "parking" to (Icons.Filled.LocalParking to "Parking"),
                                "ac" to (Icons.Filled.AcUnit to "AC"),
                                "tv" to (Icons.Filled.Tv to "TV"),
                                "spa" to (Icons.Filled.Spa to "Spa"),
                                "beach" to (Icons.Filled.BeachAccess to "Beach"),
                                "bar" to (Icons.Filled.LocalBar to "Bar")
                            )
                            val displayAmenities = if (!hotel.amenities.isNullOrEmpty()) {
                                hotel.amenities.take(4).mapNotNull { amenity ->
                                    amenityIcons.entries.find { (key, _) -> key.equals(amenity, ignoreCase = true) }?.value
                                }.ifEmpty { listOf(Icons.Filled.Wifi to "Free Wifi", Icons.Filled.Pool to "Pool", Icons.Filled.FitnessCenter to "Gym", Icons.Filled.LocalDining to "Restaurant") }
                            } else {
                                listOf(Icons.Filled.Wifi to "Free Wifi", Icons.Filled.Pool to "Pool", Icons.Filled.FitnessCenter to "Gym", Icons.Filled.LocalDining to "Restaurant")
                            }
                            displayAmenities.forEach { (icon, label) ->
                                FacilityItem(icon = icon, label = label, isDarkMode = isDarkMode)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = dividerColor)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Host Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val hostAvatar = hotel.hostAvatarUrl?.takeIf { it.isNotBlank() } ?: getDefaultAvatarUrl(hotel.hostName.orEmpty())
                            AsyncImage(model = hostAvatar, contentDescription = "Host", modifier = Modifier.size(50.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = "Hosted by", color = subTextColor, fontSize = 12.sp)
                                Text(text = hotel.hostName.orEmpty().ifEmpty { "Host" }, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = dividerColor)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Description
                        Text(text = "About this place", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = hotel.description.orEmpty().ifBlank {
                                "Experience a wonderful stay at ${hotel.title}, a perfect blend of modern design and comfort. Located in the prime area of ${hotel.location}, this property offers you a private space, an infinity pool, and the finest 5-star services."
                            },
                            color = AppColors.textSecondary(isDarkMode), fontSize = 15.sp, lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                        HorizontalDivider(color = dividerColor, thickness = 6.dp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Reviews Section
                        Text(text = "Guest Reviews", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(16.dp))

                        reviews.forEach { review ->
                            DetailReviewItem(
                                name = review.userName,
                                date = formatDate(review.createdAt),
                                content = review.content,
                                avatarUrl = review.userAvatarUrl?.takeIf { it.isNotBlank() } ?: getDefaultAvatarUrl(review.userName),
                                isDarkMode = isDarkMode
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = dividerColor.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        if (reviews.isEmpty()) {
                            Text(
                                text = "No reviews yet. Be the first to review!",
                                fontSize = 14.sp,
                                color = subTextColor,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Review Form
                        if (hasUserReviewed) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = AppColors.CyanMain.copy(alpha = 0.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = AppColors.CyanMain, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "You have reviewed this hotel",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.CyanMain
                                    )
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = userReview, onValueChange = { userReview = it },
                                placeholder = { Text("Share your experience...", color = subTextColor, fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = dividerColor, focusedBorderColor = AppColors.CyanMain,
                                    focusedTextColor = textColor, unfocusedTextColor = textColor,
                                    cursorColor = AppColors.CyanMain,
                                    unfocusedContainerColor = AppColors.card(isDarkMode), focusedContainerColor = AppColors.card(isDarkMode)
                                )
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                Button(
                                    onClick = {
                                        if (userReview.isNotBlank()) {
                                            viewModel.submitReview(
                                                hotelId = hotel.id,
                                                content = userReview,
                                                onSuccess = {
                                                    userReview = ""
                                                    Toast.makeText(context, "Review submitted!", Toast.LENGTH_SHORT).show()
                                                },
                                                onError = { error ->
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                                }
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.CyanMain),
                                    shape = RoundedCornerShape(14.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                    enabled = !isSubmittingReview && userReview.isNotBlank()
                                ) {
                                    if (isSubmittingReview) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Submit Review", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

private fun formatDate(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val date = inputFormat.parse(isoDate)
        date?.let { outputFormat.format(it) } ?: isoDate
    } catch (_: Exception) {
        isoDate.take(10).replace("-", "/")
    }
}

@Composable
fun FacilityItem(icon: ImageVector, label: String, isDarkMode: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(60.dp), shape = RoundedCornerShape(18.dp),
            color = if (isDarkMode) AppColors.DarkElevated else AppColors.CyanSurface
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = AppColors.CyanMain, modifier = Modifier.size(26.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = AppColors.textSecondary(isDarkMode))
    }
}

@Composable
fun DetailReviewItem(name: String, date: String, content: String, avatarUrl: String, isDarkMode: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AppColors.textPrimary(isDarkMode))
                Text(text = date, fontSize = 12.sp, color = AppColors.textTertiary(isDarkMode))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = content, color = AppColors.textSecondary(isDarkMode), fontSize = 14.sp, lineHeight = 22.sp)
    }
}
