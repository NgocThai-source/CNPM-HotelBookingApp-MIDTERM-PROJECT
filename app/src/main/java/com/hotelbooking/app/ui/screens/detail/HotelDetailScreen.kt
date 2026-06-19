package com.hotelbooking.app.ui.screens.detail

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hotelbooking.app.data.model.Room
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Locale

fun getDefaultAvatarUrl(userName: String): String {
    val encoded = java.net.URLEncoder.encode(userName.ifBlank { "User" }, "UTF-8")
    return "https://ui-avatars.com/api/?name=$encoded&background=1C2B4A&color=2EB8E6&size=128"
}

// ── Amenity icon map (shared with Search Screen) ─────────────────────────────
private val amenityIconMap = mapOf(
    "wifi"       to (Icons.Filled.Wifi        to "Free Wifi"),
    "pool"       to (Icons.Filled.Pool        to "Pool"),
    "gym"        to (Icons.Filled.FitnessCenter to "Gym"),
    "restaurant" to (Icons.Filled.LocalDining  to "Restaurant"),
    "parking"    to (Icons.Filled.LocalParking to "Parking"),
    "ac"         to (Icons.Filled.AcUnit       to "AC"),
    "tv"         to (Icons.Filled.Tv           to "TV"),
    "spa"        to (Icons.Filled.Spa          to "Spa"),
    "beach"      to (Icons.Filled.BeachAccess  to "Beach"),
    "bar"        to (Icons.Filled.LocalBar     to "Bar"),
)

// ════════════════════════════════════════════════════════════════════
// HOTEL DETAIL SCREEN
// ════════════════════════════════════════════════════════════════════
@Composable
fun HotelDetailScreen(
    navController: NavController,
    hotelId: String,
    isDarkMode: Boolean = false,
    viewModel: HotelDetailViewModel
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

    // Warm cream bg matching HomeScreen
    val screenBg = if (isDarkMode)
        Brush.verticalGradient(listOf(AppColors.DarkBackground, Color(0xFF0D1520), AppColors.DarkBackground))
    else
        Brush.verticalGradient(listOf(AppColors.CreamLight, AppColors.CreamSurface, Color(0xFFF2EDE5)))

    when (detailState) {
        is HotelDetailState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    if (isDarkMode) AppColors.DarkBackground else AppColors.CreamSurface
                ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = AppColors.SkyBrand, strokeWidth = 3.dp, modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("Loading hotel...", fontSize = 14.sp, color = AppColors.textSecondary(isDarkMode))
                }
            }
        }

        is HotelDetailState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    if (isDarkMode) AppColors.DarkBackground else AppColors.CreamSurface
                ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(AppColors.Error.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.ErrorOutline, contentDescription = null, modifier = Modifier.size(36.dp), tint = AppColors.Error)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Failed to load hotel", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary(isDarkMode))
                    Spacer(Modifier.height(6.dp))
                    Text(detailState.message, fontSize = 13.sp, color = AppColors.textSecondary(isDarkMode))
                    Spacer(Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Brush.horizontalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid)))
                            .clickable { viewModel.fetchHotelDetail(hotelId) }
                            .padding(horizontal = 32.dp, vertical = 13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Try Again", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                }
            }
        }

        is HotelDetailState.Success -> {
            val hotel = detailState.hotel
            val resolvedHostName = hotel.resolvedHostName.ifEmpty { "Host" }

            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    // ── Bottom Bar ─────────────────────────────────────────
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 20.dp,
                        color = if (isDarkMode) AppColors.DarkCard else Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Price per night",
                                    fontSize = 11.sp,
                                    color = AppColors.textSecondary(isDarkMode)
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$${hotel.price.toInt()}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AppColors.SkyBrand
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        text = "/night",
                                        fontSize = 12.sp,
                                        color = AppColors.textSecondary(isDarkMode),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .height(50.dp)
                                    .shadow(
                                        elevation = 8.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        ambientColor = AppColors.NavyDeep.copy(alpha = 0.3f),
                                        spotColor = AppColors.NavyDeep.copy(alpha = 0.4f)
                                    )
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(AppColors.NavyDeep, AppColors.NavyMid)
                                        )
                                    )
                                    .clickable {
                                        val route = Routes.roomSelectRoute(
                                            hotelId = hotel.id,
                                            hotelTitle = hotel.title,
                                            hotelImageUrl = hotel.imageUrl,
                                            exchangeRate = exchangeRate
                                        )
                                        navController.navigate(route)
                                    }
                                    .padding(horizontal = 28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Book Now",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(screenBg)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // ── Hero Image ─────────────────────────────────────
                        Box(modifier = Modifier.fillMaxWidth().height(290.dp)) {
                            AsyncImage(
                                model = hotel.imageUrl,
                                contentDescription = hotel.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Dark gradient overlay at bottom (fade to bg)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.55f)
                                            )
                                        )
                                    )
                            )

                            // Navy top overlay for nav buttons readability
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .align(Alignment.TopCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent)
                                        )
                                    )
                            )

                            // Price glass badge (bottom-left on image, like PropertyCard)
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.22f),
                                                Color.White.copy(alpha = 0.14f)
                                            )
                                        )
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = "$${hotel.price.toInt()}/night",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Rating badge (bottom-right on image)
                            if (hotel.rating > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(AppColors.SkyBrand.copy(alpha = 0.85f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = String.format(Locale.US, "%.1f", hotel.rating),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        if (hotel.reviewCount > 0) {
                                            Text(
                                                "(${hotel.reviewCount})",
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.85f)
                                            )
                                        }
                                    }
                                }
                            }

                            // ── Nav buttons ──────────────────────────────────
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .align(Alignment.TopStart),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Back
                                var backPressed by remember { mutableStateOf(false) }
                                val backScale by animateFloatAsState(
                                    targetValue = if (backPressed) 0.88f else 1f,
                                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
                                    label = "back_scale",
                                    finishedListener = { backPressed = false }
                                )
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .graphicsLayer { scaleX = backScale; scaleY = backScale }
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.92f))
                                        .clickable {
                                            backPressed = true
                                            navController.popBackStack()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = AppColors.NavyDeep,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Favorite
                                var heartPressed by remember { mutableStateOf(false) }
                                val heartScale by animateFloatAsState(
                                    targetValue = if (heartPressed) 1.3f else 1f,
                                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessHigh),
                                    label = "heart_scale",
                                    finishedListener = { heartPressed = false }
                                )
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .graphicsLayer { scaleX = heartScale; scaleY = heartScale }
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.92f))
                                        .clickable {
                                            heartPressed = true
                                            isFavorite = !isFavorite
                                            Toast.makeText(
                                                context,
                                                if (isFavorite) "Saved to Favorites" else "Removed from Favorites",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFavorite) AppColors.Error else AppColors.NavyDeep,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // ── Main Content Card (slightly offset to overlap hero bottom) ──
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-20).dp)
                                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                                .background(if (isDarkMode) AppColors.DarkBackground else AppColors.CreamSurface)
                                .padding(top = 24.dp, bottom = 8.dp)
                        ) {

                            // ── Hotel Name + Category ──────────────────────
                            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                // Category chip + "New" tag row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    DetailAmenityChip(
                                        icon = Icons.Filled.Category,
                                        label = hotel.category.ifBlank { "Hotel" },
                                        isDarkMode = isDarkMode
                                    )
                                    if (hotel.rating == 0.0 || hotel.reviewCount == 0) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(AppColors.SkyBrand)
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("New", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                // Title
                                Text(
                                    text = hotel.title,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AppColors.textPrimary(isDarkMode),
                                    letterSpacing = (-0.4).sp,
                                    lineHeight = 28.sp
                                )

                                Spacer(Modifier.height(6.dp))

                                // Location
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.LocationOn,
                                        contentDescription = null,
                                        tint = AppColors.textTertiary(isDarkMode),
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        text = hotel.location,
                                        fontSize = 13.sp,
                                        color = AppColors.textSecondary(isDarkMode)
                                    )
                                }

                                // Dates row
                                val hasIn  = !hotel.checkInDate.isNullOrBlank()
                                val hasOut = !hotel.checkOutDate.isNullOrBlank()
                                if (hasIn || hasOut) {
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (hasIn) {
                                            DetailDateBadge(Icons.Filled.FlightLand, "In: ${hotel.checkInDate}")
                                        }
                                        if (hasOut) {
                                            DetailDateBadge(Icons.Filled.FlightTakeoff, "Out: ${hotel.checkOutDate}")
                                        }
                                    }
                                }
                            }

                            // ── Section divider ─────────────────────────────
                            Spacer(Modifier.height(20.dp))
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = AppColors.border(isDarkMode)
                            )
                            Spacer(Modifier.height(20.dp))

                            // ── Amenities ───────────────────────────────────
                            DetailSectionHeader(title = "Top Amenities", isDarkMode = isDarkMode)
                            Spacer(Modifier.height(14.dp))

                            val allAmenities = hotel.amenities ?: emptyList()
                            val iconAmenities = allAmenities
                                .take(4)
                                .mapNotNull { key ->
                                    amenityIconMap.entries
                                        .find { it.key.equals(key, ignoreCase = true) }
                                        ?.value
                                }
                                .ifEmpty {
                                    listOf(
                                        Icons.Filled.Wifi to "Free Wifi",
                                        Icons.Filled.Pool to "Pool",
                                        Icons.Filled.FitnessCenter to "Gym",
                                        Icons.Filled.LocalDining to "Restaurant"
                                    )
                                }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                iconAmenities.forEach { (icon, label) ->
                                    FacilityIconItem(icon = icon, label = label, isDarkMode = isDarkMode)
                                }
                            }

                            // Extra amenity chips (all beyond 4)
                            val extraAmenities = allAmenities.drop(4).filter { it.isNotBlank() }
                            if (extraAmenities.isNotEmpty()) {
                                Spacer(Modifier.height(12.dp))
                                val chunks = extraAmenities.chunked(4)
                                Column(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    chunks.forEach { row ->
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            row.forEach { amenity ->
                                                val icon = amenityIconMap.entries
                                                    .find { it.key.equals(amenity, ignoreCase = true) }
                                                    ?.value?.first
                                                DetailAmenityChip(
                                                    icon = icon ?: Icons.Filled.CheckCircle,
                                                    label = amenity.replaceFirstChar { it.uppercase() },
                                                    isDarkMode = isDarkMode
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // ── Section divider ─────────────────────────────
                            Spacer(Modifier.height(20.dp))
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = AppColors.border(isDarkMode)
                            )
                            Spacer(Modifier.height(20.dp))

                            // ── Host Info ───────────────────────────────────
                            DetailSectionHeader(title = "Hosted by", isDarkMode = isDarkMode)
                            Spacer(Modifier.height(14.dp))

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val hostAvatar = hotel.hostAvatarUrl?.takeIf { it.isNotBlank() }
                                    ?: getDefaultAvatarUrl(resolvedHostName)
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .shadow(4.dp, CircleShape, ambientColor = AppColors.NavyDeep.copy(alpha = 0.15f))
                                ) {
                                    AsyncImage(
                                        model = hostAvatar,
                                        contentDescription = "Host",
                                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = resolvedHostName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = AppColors.textPrimary(isDarkMode)
                                    )
                                    if (!hotel.hostEmail.isNullOrBlank()) {
                                        Spacer(Modifier.height(3.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.Email,
                                                contentDescription = null,
                                                tint = AppColors.SkyBrand,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = hotel.hostEmail,
                                                fontSize = 12.sp,
                                                color = AppColors.textSecondary(isDarkMode),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }

                            // ── Section divider ─────────────────────────────
                            Spacer(Modifier.height(20.dp))
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = AppColors.border(isDarkMode)
                            )
                            Spacer(Modifier.height(20.dp))

                            // ── About ───────────────────────────────────────
                            DetailSectionHeader(title = "About this place", isDarkMode = isDarkMode)
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = hotel.description.orEmpty().ifBlank {
                                    "Experience a wonderful stay at ${hotel.title}, a perfect blend of modern design " +
                                    "and comfort. Located in ${hotel.location}, this property provides top-tier " +
                                    "services and a memorable experience for every guest."
                                },
                                fontSize = 14.sp,
                                color = AppColors.textSecondary(isDarkMode),
                                lineHeight = 22.sp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )

                            // ── Available Rooms ──────────────────────────────
                            val activeRooms = hotel.rooms?.filter { it.isActive } ?: emptyList()
                            if (activeRooms.isNotEmpty()) {
                                Spacer(Modifier.height(24.dp))
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    color = AppColors.border(isDarkMode)
                                )
                                Spacer(Modifier.height(20.dp))
                                DetailSectionHeader(title = "Available Rooms", isDarkMode = isDarkMode)
                                Spacer(Modifier.height(14.dp))

                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    activeRooms.forEach { room ->
                                        RoomCard(room = room, isDarkMode = isDarkMode)
                                    }
                                }
                            }

                            // ── Reviews ─────────────────────────────────────
                            Spacer(Modifier.height(24.dp))
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                color = AppColors.border(isDarkMode)
                            )
                            Spacer(Modifier.height(20.dp))
                            DetailSectionHeader(title = "Guest Reviews", isDarkMode = isDarkMode)
                            Spacer(Modifier.height(14.dp))

                            if (reviews.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Filled.RateReview,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = AppColors.textTertiary(isDarkMode).copy(alpha = 0.4f)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "No reviews yet. Be the first!",
                                        fontSize = 13.sp,
                                        color = AppColors.textTertiary(isDarkMode)
                                    )
                                }
                            } else {
                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    reviews.forEachIndexed { index, review ->
                                        DetailReviewItem(
                                            name = review.userName,
                                            date = formatDate(review.createdAt),
                                            content = review.content,
                                            avatarUrl = review.userAvatarUrl
                                                ?.takeIf { it.isNotBlank() }
                                                ?: getDefaultAvatarUrl(review.userName),
                                            isDarkMode = isDarkMode
                                        )
                                        if (index < reviews.lastIndex) {
                                            Spacer(Modifier.height(12.dp))
                                            HorizontalDivider(color = AppColors.border(isDarkMode).copy(alpha = 0.5f))
                                            Spacer(Modifier.height(12.dp))
                                        }
                                    }
                                }
                            }

                            // ── Review form ─────────────────────────────────
                            Spacer(Modifier.height(16.dp))
                            if (hasUserReviewed) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(AppColors.SkyBrand.copy(alpha = 0.1f))
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = AppColors.SkyBrand, modifier = Modifier.size(20.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        "You have reviewed this hotel",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.SkyBrand
                                    )
                                }
                            } else {
                                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                                    OutlinedTextField(
                                        value = userReview,
                                        onValueChange = { userReview = it },
                                        placeholder = {
                                            Text("Share your experience...", color = AppColors.textTertiary(isDarkMode), fontSize = 13.sp)
                                        },
                                        modifier = Modifier.fillMaxWidth().height(96.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedBorderColor = AppColors.border(isDarkMode),
                                            focusedBorderColor = AppColors.SkyBrand,
                                            focusedTextColor = AppColors.textPrimary(isDarkMode),
                                            unfocusedTextColor = AppColors.textPrimary(isDarkMode),
                                            cursorColor = AppColors.SkyBrand,
                                            unfocusedContainerColor = if (isDarkMode) AppColors.DarkCard else Color.White,
                                            focusedContainerColor = if (isDarkMode) AppColors.DarkCard else Color.White
                                        )
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (!isSubmittingReview && userReview.isNotBlank())
                                                        Brush.horizontalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid))
                                                    else
                                                        Brush.horizontalGradient(listOf(AppColors.textTertiary(isDarkMode), AppColors.textTertiary(isDarkMode)))
                                                )
                                                .clickable(enabled = !isSubmittingReview && userReview.isNotBlank()) {
                                                    viewModel.submitReview(
                                                        hotelId = hotel.id,
                                                        content = userReview,
                                                        onSuccess = {
                                                            userReview = ""
                                                            Toast.makeText(context, "Review submitted!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        onError = { err ->
                                                            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                                        }
                                                    )
                                                }
                                                .padding(horizontal = 22.dp, vertical = 11.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSubmittingReview) {
                                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                            } else {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                                    Spacer(Modifier.width(6.dp))
                                                    Text("Submit Review", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(28.dp))
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// ROOM CARD (matches PropertyCard style)
// ════════════════════════════════════════════════════════════════════
@Composable
private fun RoomCard(room: Room, isDarkMode: Boolean) {
    val displayName = room.roomName?.takeIf { it.isNotBlank() }
        ?: room.roomType?.takeIf { it.isNotBlank() }
        ?: room.type.ifBlank { "Room" }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDarkMode) 0.dp else 8.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = AppColors.NavyDeep.copy(alpha = 0.06f),
                spotColor = AppColors.NavyDeep.copy(alpha = 0.10f)
            )
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Room image
                val roomImage = room.images?.firstOrNull()?.takeIf { it.isNotBlank() }
                if (roomImage != null) {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        AsyncImage(
                            model = roomImage,
                            contentDescription = displayName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        )
                        // Price badge on room image
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.White.copy(alpha = 0.22f), Color.White.copy(alpha = 0.14f))
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                "$${room.pricePerNight.toInt()}/night",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    // Name + price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AppColors.textPrimary(isDarkMode),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (room.type.isNotBlank() && !room.type.equals(displayName, ignoreCase = true)) {
                                Text(
                                    text = room.type,
                                    fontSize = 12.sp,
                                    color = AppColors.textSecondary(isDarkMode)
                                )
                            }
                        }
                        if (roomImage == null) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$${room.pricePerNight.toInt()}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 17.sp,
                                    color = AppColors.SkyBrand
                                )
                                Text(text = "/ night", fontSize = 11.sp, color = AppColors.textSecondary(isDarkMode))
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Capacity + availability chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoomInfoTag(
                            icon = Icons.Filled.Person,
                            label = "${room.capacity} guest${if (room.capacity > 1) "s" else ""}",
                            isDarkMode = isDarkMode
                        )
                        if (room.availableQuantity > 0) {
                            RoomInfoTag(
                                icon = Icons.Filled.MeetingRoom,
                                label = "${room.availableQuantity} left",
                                isDarkMode = isDarkMode
                            )
                        }
                    }

                    // Room dates
                    val hasIn  = !room.checkInDate.isNullOrBlank()
                    val hasOut = !room.checkOutDate.isNullOrBlank()
                    if (hasIn || hasOut) {
                        Spacer(Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (hasIn)  DetailDateBadge(Icons.Filled.FlightLand, "In: ${room.checkInDate}")
                            if (hasOut) DetailDateBadge(Icons.Filled.FlightTakeoff, "Out: ${room.checkOutDate}")
                        }
                    }

                    // Amenity chips (room-level, SkyBrand style matching PropertyCard)
                    val roomAmenities = room.amenities?.filter { it.isNotBlank() } ?: emptyList()
                    if (roomAmenities.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            roomAmenities.take(5).forEach { amenity ->
                                val icon = amenityIconMap.entries
                                    .find { it.key.equals(amenity, ignoreCase = true) }
                                    ?.value?.first ?: Icons.Filled.CheckCircle
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AppColors.SkyBrand.copy(alpha = 0.1f))
                                        .padding(horizontal = 7.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(icon, contentDescription = amenity, tint = AppColors.SkyBrand, modifier = Modifier.size(11.dp))
                                        Text(
                                            amenity.replaceFirstChar { it.uppercase() },
                                            fontSize = 10.sp,
                                            color = AppColors.SkyBrand,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Description
                    if (!room.description.isNullOrBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = room.description,
                            fontSize = 12.sp,
                            color = AppColors.textSecondary(isDarkMode),
                            lineHeight = 18.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// SMALL COMPOSABLES
// ════════════════════════════════════════════════════════════════════

/** Section title — 20sp ExtraBold matching SectionHeaderPremium in HomeScreen */
@Composable
private fun DetailSectionHeader(title: String, isDarkMode: Boolean) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(22.dp)
                .background(
                    Brush.verticalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid)),
                    RoundedCornerShape(2.dp)
                )
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AppColors.textPrimary(isDarkMode),
            letterSpacing = (-0.3).sp
        )
    }
}

/** Large facility icon + label (for Top Amenities grid) */
@Composable
fun FacilityIconItem(icon: ImageVector, label: String, isDarkMode: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(AppColors.SkyBrand.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = AppColors.SkyBrand, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(text = label, fontSize = 11.sp, color = AppColors.textSecondary(isDarkMode), fontWeight = FontWeight.Medium)
    }
}

/** Small chip with icon + label (SkyBrand style, matching PropertyCard amenity) */
@Composable
private fun DetailAmenityChip(icon: ImageVector, label: String, isDarkMode: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppColors.SkyBrand.copy(alpha = 0.1f))
            .padding(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = label, tint = AppColors.SkyBrand, modifier = Modifier.size(12.dp))
            Text(label, fontSize = 11.sp, color = AppColors.SkyBrand, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Date badge — icon + text (SkyBrand) */
@Composable
private fun DetailDateBadge(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = AppColors.SkyBrand, modifier = Modifier.size(12.dp))
        Spacer(Modifier.width(3.dp))
        Text(label, fontSize = 11.sp, color = AppColors.SkyBrand, fontWeight = FontWeight.Medium)
    }
}

/** Inline room info (icon + text) */
@Composable
private fun RoomInfoTag(icon: ImageVector, label: String, isDarkMode: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(icon, contentDescription = null, tint = AppColors.textTertiary(isDarkMode), modifier = Modifier.size(13.dp))
        Text(text = label, fontSize = 12.sp, color = AppColors.textSecondary(isDarkMode))
    }
}

/** Review item */
@Composable
fun DetailReviewItem(name: String, date: String, content: String, avatarUrl: String, isDarkMode: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AppColors.textPrimary(isDarkMode))
                Text(text = date, fontSize = 11.sp, color = AppColors.textTertiary(isDarkMode))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(text = content, color = AppColors.textSecondary(isDarkMode), fontSize = 13.sp, lineHeight = 20.sp)
    }
}

// ════════════════════════════════════════════════════════════════════
// LEGACY aliases (kept so existing call-sites compile)
// ════════════════════════════════════════════════════════════════════
@Composable
fun FacilityItem(icon: ImageVector, label: String, isDarkMode: Boolean) =
    FacilityIconItem(icon, label, isDarkMode)

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
