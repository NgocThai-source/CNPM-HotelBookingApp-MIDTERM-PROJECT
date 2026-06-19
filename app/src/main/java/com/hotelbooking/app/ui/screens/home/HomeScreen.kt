package com.hotelbooking.app.ui.screens.home

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hotelbooking.app.R
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.ui.components.AppBottomNavBar
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.screens.profile.FavoritesViewModel
import com.hotelbooking.app.ui.screens.profile.FavoritesState
import com.hotelbooking.app.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

// Warm Cream palette (spec: #F7F3EC)
private val WarmSurface = AppColors.CreamSurface
private val WarmSurfaceLight = AppColors.CreamLight

// ── Top Destinations data ──────────────────────────────────────────
private data class Destination(val name: String, val imageRes: Int)

private val topDestinations = listOf(
    Destination("Da Nang", R.drawable.da_nang),
    Destination("Hoi An", R.drawable.hoi_an),
    Destination("Ha Noi", R.drawable.ha_noi),
    Destination("Ho Chi Minh", R.drawable.sai_gon),
    Destination("Nha Trang", R.drawable.nha_trang),
    Destination("Hue", R.drawable.hue),
)

// ── Time-based greeting ────────────────────────────────────────────
@Composable
private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}

// ── Shimmer brush ──────────────────────────────────────────────────
@Composable
private fun shimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )
    return Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.3f),
            Color.LightGray.copy(alpha = 0.7f),
            Color.LightGray.copy(alpha = 0.3f)
        ),
        start = Offset(progress * 1000f - 200f, 0f),
        end = Offset(progress * 1000f, 0f)
    )
}

// ── Shimmer property card skeleton ───────────────────────────────
@Composable
private fun ShimmerPropertyCard() {
    val brush = shimmerBrush()
    val cardRadius = 24.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(360.dp),
        shape = RoundedCornerShape(cardRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(topStart = cardRadius, topEnd = cardRadius))
                    .background(brush)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(18.dp)
                        .background(brush, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .background(brush, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(brush, RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// HOME SCREEN
// ════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, isDarkMode: Boolean, onThemeToggle: () -> Unit) {
    val context = LocalContext.current
    Log.d("HomeScreen", ">>> HomeScreen composable started")
    val viewModel: HomeViewModel = viewModel()
    val hotels by viewModel.hotels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val favoritesState by FavoritesViewModel.state.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(error) {
        if (error != null) {
            Log.e("HomeScreen", ">>> ERROR state: $error")
            Toast.makeText(context, "API Error: $error", Toast.LENGTH_LONG).show()
        }
    }

    var selectedCategory by remember { mutableStateOf("All Stays") }
    var guestCount by remember { mutableStateOf(2) }
    var dateRange by remember { mutableStateOf("Oct 12 - 15") }
    var searchQuery by remember { mutableStateOf("") }
    var showGuestDialog by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var priceRange by remember { mutableStateOf(0f..500000f) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateRangePickerState = rememberDateRangePickerState()

    val displayedHotels = hotels.filter { item ->
        val matchCategory = selectedCategory == "All Stays" || item.category == selectedCategory ||
            (selectedCategory == "Resorts" && item.category == "Resort") ||
            (selectedCategory == "Boutique" && item.category == "Boutique") ||
            (selectedCategory == "Luxury" && item.category == "Luxury")
        val matchPrice = item.price >= priceRange.start && item.price <= priceRange.endInclusive
        val matchSearch = item.title.contains(searchQuery, ignoreCase = true) ||
            item.location.contains(searchQuery, ignoreCase = true)
        matchCategory && matchPrice && matchSearch
    }.sortedBy { it.price }

    val featuredHotel = hotels.maxByOrNull { it.rating }

    // Warm cream background matching Auth
    val screenBackground = if (isDarkMode) {
        Brush.verticalGradient(
            listOf(AppColors.DarkBackground, Color(0xFF0D1520), AppColors.DarkBackground)
        )
    } else {
        Brush.verticalGradient(
            listOf(WarmSurfaceLight, WarmSurface, Color(0xFFF2EDE5))
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            AppBottomNavBar(
                isDarkMode = isDarkMode,
                currentRoute = Routes.HOME,
                onNavigate = { route -> navController.navigate(route) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackground)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // ── HEADER + SEARCH BAR (overlap: search sits in the middle) ──
                item {
                    PremiumHeaderWithSearch(
                        isDarkMode = isDarkMode,
                        onThemeToggle = onThemeToggle,
                        onReloadClick = { viewModel.refreshAll() },
                        isLoading = isLoading,
                        greeting = getGreeting(),
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        dateRange = dateRange,
                        guestCount = guestCount,
                        onDateClick = { showDateDialog = true },
                        onGuestClick = { showGuestDialog = true },
                        onFilterClick = { showFilterSheet = true },
                        userName = userProfile?.fullName ?: "",
                        userEmail = userProfile?.email ?: ""
                    )
                }

                // ── CATEGORY CHIPS ──────────────────────────────────────
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryChips(
                        currentCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        isDarkMode = isDarkMode
                    )
                }

                // ── TOP DESTINATIONS ────────────────────────────────────
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeaderPremium("Explore Destinations", isDarkMode)
                    Spacer(modifier = Modifier.height(12.dp))
                    TopDestinationsRow(isDarkMode)
                }

                // ── FEATURED HERO ────────────────────────────────────────
                if (!isLoading && featuredHotel != null) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeaderPremium("Featured for You", isDarkMode)
                        Spacer(modifier = Modifier.height(12.dp))
                        FeaturedHeroCard(
                            hotel = featuredHotel,
                            isFavorite = favoritesState.favorites.any { it.id == featuredHotel.id },
                            onFavoriteToggle = {
                                if (favoritesState.favorites.any { it.id == featuredHotel.id }) {
                                    FavoritesViewModel.removeFavorite(featuredHotel.id)
                                } else {
                                    FavoritesViewModel.addFavorite(featuredHotel.id)
                                }
                            },
                            isDarkMode = isDarkMode,
                            onClick = {
                                navController.navigate(Routes.DETAIL.replace("{hotelId}", featuredHotel.id))
                            }
                        )
                    }
                }

                // ── TOP RATED CAROUSEL ──────────────────────────────────
                if (!isLoading && hotels.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionHeaderPremium("Top Rated", isDarkMode)
                        Spacer(modifier = Modifier.height(12.dp))
                        TopRatedCarousel(
                            hotels = hotels.sortedByDescending { it.rating }.take(8),
                            favoritesState = favoritesState,
                            isDarkMode = isDarkMode,
                            onHotelClick = { hotel ->
                                navController.navigate(Routes.DETAIL.replace("{hotelId}", hotel.id))
                            }
                        )
                    }
                }

                // ── AVAILABLE HOTELS SECTION ───────────────────────────
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeaderPremium("Available Hotels", isDarkMode)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isLoading) {
                    items(3) {
                        ShimmerPropertyCard()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else if (error != null && hotels.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.CloudOff, contentDescription = null,
                                modifier = Modifier.size(52.dp),
                                tint = AppColors.textTertiary(isDarkMode).copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "Could not load hotels",
                                color = AppColors.textSecondary(isDarkMode), fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(AppColors.NavyDeep, AppColors.NavyMid)
                                        )
                                    )
                                    .clickable { viewModel.fetchHotels() }
                                    .padding(horizontal = 28.dp, vertical = 12.dp)
                            ) {
                                Text("Retry", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                } else if (displayedHotels.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.SearchOff, contentDescription = null,
                                modifier = Modifier.size(52.dp),
                                tint = AppColors.textTertiary(isDarkMode).copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                "No matching hotels found.",
                                color = AppColors.textSecondary(isDarkMode), fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    items(displayedHotels) { hotel ->
                        PropertyCard(
                            hotel = hotel,
                            isFavorite = favoritesState.favorites.any { it.id == hotel.id },
                            onFavoriteToggle = {
                                if (favoritesState.favorites.any { it.id == hotel.id }) {
                                    FavoritesViewModel.removeFavorite(hotel.id)
                                } else {
                                    FavoritesViewModel.addFavorite(hotel.id)
                                }
                            },
                            isDarkMode = isDarkMode,
                            onClick = {
                                navController.navigate(Routes.DETAIL.replace("{hotelId}", hotel.id))
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

    // ── Filter Bottom Sheet ──────────────────────────────────────
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = if (isDarkMode) AppColors.DarkCard else WarmSurface,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(4.dp, 32.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(AppColors.NavyDeep, AppColors.NavyMid)
                                ),
                                RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Advanced Filters", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary(isDarkMode)
                    )
                }
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            "Price Range (Per Night)", fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp, color = AppColors.textPrimary(isDarkMode)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AppColors.SkyBrand.copy(alpha = 0.12f)
                        ) {
                            Text(
                                "$${priceRange.start.roundToInt()} - $${priceRange.endInclusive.roundToInt()}",
                                color = AppColors.SkyBrand, fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    RangeSlider(
                        value = priceRange, onValueChange = { priceRange = it },
                        valueRange = 0f..500000f, steps = 25,
                        colors = SliderDefaults.colors(
                            thumbColor = AppColors.SkyBrand,
                            activeTrackColor = AppColors.SkyBrand,
                            inactiveTrackColor = if (isDarkMode) AppColors.DarkBorder else AppColors.WarmBorder
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(AppColors.NavyDeep, AppColors.NavyMid)
                            )
                        )
                        .clickable { showFilterSheet = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Show Results", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // ── Guest Dialog ─────────────────────────────────────────────
    if (showGuestDialog) {
        AlertDialog(
            onDismissRequest = { showGuestDialog = false },
            containerColor = if (isDarkMode) AppColors.DarkCard else WarmSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    "Select Guests", fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color = AppColors.textPrimary(isDarkMode)
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Adults:", fontSize = 16.sp, color = AppColors.textPrimary(isDarkMode))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (guestCount > 1) guestCount-- }) {
                            Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Decrease", tint = AppColors.SkyBrand)
                        }
                        Text(
                            "$guestCount", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = AppColors.textPrimary(isDarkMode)
                        )
                        IconButton(onClick = { if (guestCount < 10) guestCount++ }) {
                            Icon(Icons.Filled.AddCircleOutline, contentDescription = "Increase", tint = AppColors.SkyBrand)
                        }
                    }
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid))
                        )
                        .clickable { showGuestDialog = false }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                }
            }
        )
    }

    // ── Date Picker Dialog ──────────────────────────────────────
    if (showDateDialog) {
        Dialog(
            onDismissRequest = { showDateDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.85f),
                shape = RoundedCornerShape(24.dp),
                color = if (isDarkMode) AppColors.DarkCard else WarmSurface
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        DateRangePicker(
                            state = dateRangePickerState,
                            title = {
                                Text(
                                    "Select Dates",
                                    modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                                    color = AppColors.textPrimary(isDarkMode),
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            headline = {
                                Text(
                                    "Check-in - Check-out",
                                    modifier = Modifier.padding(start = 24.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.textPrimary(isDarkMode)
                                )
                            },
                            showModeToggle = false,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDateDialog = false }) {
                            Text("Cancel", color = AppColors.textSecondary(isDarkMode))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid))
                                )
                                .clickable {
                                    val startMillis = dateRangePickerState.selectedStartDateMillis
                                    val endMillis = dateRangePickerState.selectedEndDateMillis
                                    if (startMillis != null && endMillis != null) {
                                        val formatter = SimpleDateFormat("MMM dd", Locale.US)
                                        dateRange = "${formatter.format(java.util.Date(startMillis))} - ${formatter.format(java.util.Date(endMillis))}"
                                    }
                                    showDateDialog = false
                                }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text("Confirm", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// PREMIUM HEADER + SEARCH BAR (overlap layout)
// ════════════════════════════════════════════════════════════════════
@Composable
private fun PremiumHeaderWithSearch(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    onReloadClick: () -> Unit,
    isLoading: Boolean,
    greeting: String,
    query: String,
    onQueryChange: (String) -> Unit,
    dateRange: String,
    guestCount: Int,
    onDateClick: () -> Unit,
    onGuestClick: () -> Unit,
    onFilterClick: () -> Unit,
    userName: String = "",
    userEmail: String = ""
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600, delayMillis = 100),
        label = "header_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha }
    ) {
        // ── Gradient navy background ────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AppColors.NavyDeep, AppColors.NavyMid)
                    )
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
                        colors = listOf(
                            AppColors.SkyBrand.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )

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
                            if (isDarkMode) AppColors.DarkBackground else WarmSurface
                        )
                    )
                )
        )

        // ── Header content (statusBarsPadding + horizontal padding) ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            // Top row: logo + action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Text(
                            text = if (userName.isNotBlank()) "Hi, $userName" else "Hi, there",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (userEmail.isNotBlank()) userEmail else "Premium Hotels",
                            fontSize = 10.sp,
                            color = AppColors.SkyBrand.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = AppColors.SkyBrand
                        )
                    } else {
                        IconButton(onClick = onReloadClick) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Reload",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.WbSunny else Icons.Filled.NightsStay,
                            contentDescription = "Toggle Theme",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Greeting + heading
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    "$greeting,",
                    fontSize = 13.sp,
                    color = AppColors.SkyBrand.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Find your dream stay",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 32.sp
                )
            }

            // ── SEARCH BAR (overlaps into cream zone) ────────────────
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = AppColors.NavyDeep.copy(alpha = 0.15f),
                        spotColor = AppColors.NavyDeep.copy(alpha = 0.2f)
                    ),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isDarkMode) AppColors.DarkSurface.copy(alpha = 0.6f)
                                else WarmSurfaceLight
                            )
                            .border(
                                width = 1.dp,
                                color = if (isDarkMode) AppColors.DarkBorder else AppColors.WarmBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = AppColors.SkyBrand,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.textPrimary(isDarkMode)
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (query.isEmpty()) {
                                        Text(
                                            "Search hotels or locations...",
                                            fontSize = 14.sp,
                                            color = AppColors.textTertiary(isDarkMode),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }

                    // Date + Guest + Filter row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InfoBoxPremium(
                            icon = Icons.Filled.DateRange,
                            title = "DATES",
                            value = dateRange,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onDateClick() },
                            isDarkMode = isDarkMode
                        )
                        InfoBoxPremium(
                            icon = Icons.Filled.Person,
                            title = "GUESTS",
                            value = "$guestCount Adults",
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onGuestClick() },
                            isDarkMode = isDarkMode
                        )
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(AppColors.NavyDeep, AppColors.NavyMid)
                                    )
                                )
                                .clickable { onFilterClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.FilterList,
                                contentDescription = "Filter",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// INFO BOX PREMIUM
// ════════════════════════════════════════════════════════════════════
@Composable
private fun InfoBoxPremium(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean
) {
    val bg = if (isDarkMode) AppColors.DarkElevated else WarmSurfaceLight
    val borderColor = if (isDarkMode) AppColors.DarkBorder else AppColors.WarmBorder

    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon, contentDescription = null,
            tint = AppColors.SkyBrand,
            modifier = Modifier.size(18.dp)
        )
        Column {
            Text(
                title, fontSize = 9.sp,
                color = AppColors.textTertiary(isDarkMode),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Text(
                value, fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.textPrimary(isDarkMode),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// CATEGORY CHIPS
// ════════════════════════════════════════════════════════════════════
@Composable
fun CategoryChips(
    currentCategory: String,
    onCategorySelected: (String) -> Unit,
    isDarkMode: Boolean
) {
    val categories = listOf("All Stays", "Luxury", "Resort", "Boutique", "Hotel", "Homestay", "Villa")

    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { cat ->
            val isSelected = cat == currentCategory

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) {
                    if (isDarkMode) AppColors.SkyBrand.copy(alpha = 0.2f)
                    else AppColors.SkyBrand
                } else if (isDarkMode) {
                    AppColors.DarkElevated
                } else {
                    Color.White
                },
                animationSpec = tween(250),
                label = "chip_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) {
                    if (isDarkMode) AppColors.SkyBrand else Color.White
                } else {
                    AppColors.textSecondary(isDarkMode)
                },
                animationSpec = tween(250),
                label = "chip_text"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) {
                    if (isDarkMode) AppColors.SkyBrand.copy(alpha = 0.5f)
                    else AppColors.SkyDark
                } else if (isDarkMode) {
                    AppColors.DarkBorder
                } else {
                    AppColors.WarmBorder
                },
                animationSpec = tween(250),
                label = "chip_border"
            )
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh
                ),
                label = "chip_scale"
            )

            Box(
                modifier = Modifier
                    .scale(scale)
                    .clip(RoundedCornerShape(24.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                    .clickable { onCategorySelected(cat) }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
                Text(
                    cat,
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// SECTION HEADER PREMIUM
// ════════════════════════════════════════════════════════════════════
@Composable
private fun SectionHeaderPremium(title: String, isDarkMode: Boolean) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = AppColors.textPrimary(isDarkMode),
        modifier = Modifier.padding(horizontal = 16.dp),
        letterSpacing = (-0.3).sp
    )
}

// ════════════════════════════════════════════════════════════════════
// TOP DESTINATIONS ROW
// ════════════════════════════════════════════════════════════════════
@Composable
private fun TopDestinationsRow(isDarkMode: Boolean) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(topDestinations) { dest ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(AppColors.SkyBrand, AppColors.SkyDark)
                            ),
                            shape = CircleShape
                        )
                        .clickable { }
                ) {
                    Image(
                        painter = painterResource(id = dest.imageRes),
                        contentDescription = dest.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Text(
                    dest.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textSecondary(isDarkMode)
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// FEATURED HERO CARD
// ════════════════════════════════════════════════════════════════════
@Composable
private fun FeaturedHeroCard(
    hotel: Hotel,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val cardRadius = 26.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() }
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(cardRadius),
                ambientColor = AppColors.NavyDeep.copy(alpha = 0.1f),
                spotColor = AppColors.NavyDeep.copy(alpha = 0.18f)
            ),
        shape = RoundedCornerShape(cardRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = hotel.imageUrl,
                    contentDescription = hotel.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = cardRadius, topEnd = cardRadius)),
                    contentScale = ContentScale.Crop
                )

                // Navy scrim at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    AppColors.NavyDeep.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // New badge
                if (hotel.rating > 0 && hotel.rating < 3.5) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.SkyBrand)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("New", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Price pill glass
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.25f),
                                    Color.White.copy(alpha = 0.15f)
                                )
                            )
                        )
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "$${hotel.price.toInt()}/night",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                // Heart button
                var heartPressed by remember { mutableStateOf(false) }
                val heartScale by animateFloatAsState(
                    targetValue = if (heartPressed) 1.3f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    ),
                    label = "heart_scale",
                    finishedListener = { heartPressed = false }
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(14.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .graphicsLayer { scaleX = heartScale; scaleY = heartScale }
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable {
                            heartPressed = true
                            onFavoriteToggle()
                            Toast.makeText(context, if (!isFavorite) "Saved!" else "Removed", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) AppColors.SkyBrand else AppColors.textTertiary(isDarkMode),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        hotel.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.textPrimary(isDarkMode),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star, contentDescription = null,
                            tint = AppColors.SkyBrand,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            String.format(Locale.US, "%.1f", hotel.rating),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary(isDarkMode)
                        )
                        if (hotel.reviewCount > 0) {
                            Text(
                                " (${hotel.reviewCount})",
                                fontSize = 12.sp,
                                color = AppColors.textTertiary(isDarkMode)
                            )
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn, contentDescription = null,
                        tint = AppColors.textTertiary(isDarkMode),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(hotel.location, fontSize = 13.sp, color = AppColors.textSecondary(isDarkMode))
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// TOP RATED CAROUSEL
// ════════════════════════════════════════════════════════════════════
@Composable
private fun TopRatedCarousel(
    hotels: List<Hotel>,
    favoritesState: FavoritesState,
    isDarkMode: Boolean,
    onHotelClick: (Hotel) -> Unit
) {
    val cardWidth = 180.dp

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(hotels) { hotel ->
            TopRatedMiniCard(
                hotel = hotel,
                isFavorite = favoritesState.favorites.any { it.id == hotel.id },
                onFavoriteToggle = {
                    if (favoritesState.favorites.any { it.id == hotel.id }) {
                        FavoritesViewModel.removeFavorite(hotel.id)
                    } else {
                        FavoritesViewModel.addFavorite(hotel.id)
                    }
                },
                isDarkMode = isDarkMode,
                onClick = { onHotelClick(hotel) },
                modifier = Modifier.width(cardWidth)
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// TOP RATED MINI CARD
// ════════════════════════════════════════════════════════════════════
@Composable
private fun TopRatedMiniCard(
    hotel: Hotel,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    isDarkMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(200.dp)
            .clickable { onClick() }
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = AppColors.NavyDeep.copy(alpha = 0.08f),
                spotColor = AppColors.NavyDeep.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
        )
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                AsyncImage(
                    model = hotel.imageUrl,
                    contentDescription = hotel.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid))
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("$${hotel.price.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable { onFavoriteToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) AppColors.SkyBrand else AppColors.textTertiary(isDarkMode),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    hotel.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary(isDarkMode),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    hotel.location,
                    fontSize = 11.sp,
                    color = AppColors.textTertiary(isDarkMode),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star, contentDescription = null,
                        tint = AppColors.SkyBrand,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        String.format(Locale.US, "%.1f", hotel.rating),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary(isDarkMode)
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════
// PROPERTY CARD
// ════════════════════════════════════════════════════════════════════
@Composable
fun PropertyCard(
    hotel: Hotel,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val cardRadius = 24.dp

    val amenityIcons = mapOf(
        "wifi" to Icons.Filled.Wifi,
        "pool" to Icons.Filled.Pool,
        "gym" to Icons.Filled.FitnessCenter,
        "restaurant" to Icons.Filled.LocalDining,
        "parking" to Icons.Filled.LocalParking,
        "ac" to Icons.Filled.AcUnit,
        "tv" to Icons.Filled.Tv,
        "spa" to Icons.Filled.Spa,
        "beach" to Icons.Filled.BeachAccess,
        "bar" to Icons.Filled.LocalBar
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = if (isDarkMode) 0.dp else 10.dp,
                shape = RoundedCornerShape(cardRadius),
                ambientColor = AppColors.NavyDeep.copy(alpha = 0.06f),
                spotColor = AppColors.NavyDeep.copy(alpha = 0.12f)
            )
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(cardRadius),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    AsyncImage(
                        model = hotel.imageUrl,
                        contentDescription = hotel.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = cardRadius, topEnd = cardRadius)),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                )
                            )
                    )

                    // Price badge glass pill
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.22f),
                                        Color.White.copy(alpha = 0.14f)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.3f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "$${hotel.price.toInt()}/night",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // New badge
                    if (hotel.rating > 0 && hotel.rating < 3.5) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 12.dp, start = 110.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.SkyBrand)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("New", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Heart button
                    var heartPressed by remember { mutableStateOf(false) }
                    val heartScale by animateFloatAsState(
                        targetValue = if (heartPressed) 1.3f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh
                        ),
                        label = "heart_scale",
                        finishedListener = { heartPressed = false }
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .graphicsLayer { scaleX = heartScale; scaleY = heartScale }
                            .background(
                                if (isDarkMode) AppColors.DarkCard.copy(alpha = 0.85f)
                                else Color.White.copy(alpha = 0.92f)
                            )
                            .clickable {
                                heartPressed = true
                                onFavoriteToggle()
                                Toast.makeText(context, if (!isFavorite) "Saved!" else "Removed", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = if (isFavorite) AppColors.SkyBrand else AppColors.textSecondary(isDarkMode),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        hotel.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary(isDarkMode),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocationOn, contentDescription = null,
                            tint = AppColors.textTertiary(isDarkMode),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(hotel.location, fontSize = 13.sp, color = AppColors.textSecondary(isDarkMode))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star, contentDescription = null,
                            tint = AppColors.SkyBrand,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (hotel.rating > 0) String.format(Locale.US, "%.1f", hotel.rating) else "New",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (hotel.rating > 0) AppColors.textPrimary(isDarkMode)
                            else AppColors.textTertiary(isDarkMode)
                        )
                        if (hotel.reviewCount > 0) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                "(${hotel.reviewCount})", fontSize = 12.sp,
                                color = AppColors.textTertiary(isDarkMode)
                            )
                        }
                    }

                    if (!hotel.checkInDate.isNullOrEmpty() || !hotel.checkOutDate.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!hotel.checkInDate.isNullOrEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.FlightLand, contentDescription = null,
                                        tint = AppColors.SkyBrand, modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("In: ${hotel.checkInDate}", fontSize = 11.sp, color = AppColors.textSecondary(isDarkMode))
                                }
                            }
                            if (!hotel.checkOutDate.isNullOrEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.FlightTakeoff, contentDescription = null,
                                        tint = AppColors.SkyBrand, modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Out: ${hotel.checkOutDate}", fontSize = 11.sp, color = AppColors.textSecondary(isDarkMode))
                                }
                            }
                        }
                    }

                    if (!hotel.amenities.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            hotel.amenities.take(4).forEach { amenity ->
                                val icon = amenityIcons[amenity.lowercase()]
                                if (icon != null) {
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
                                            Icon(
                                                icon, contentDescription = amenity,
                                                tint = AppColors.SkyBrand,
                                                modifier = Modifier.size(12.dp)
                                            )
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
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!hotel.hostAvatarUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = hotel.hostAvatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                hotel.hostName.orEmpty().ifEmpty { "Host" },
                                fontSize = 12.sp,
                                color = AppColors.textSecondary(isDarkMode)
                            )
                        }
                        if (!hotel.badgeText.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AppColors.SkyBrand.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    hotel.badgeText,
                                    fontSize = 10.sp,
                                    color = AppColors.SkyBrand,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Bottom Navigation delegate ────────────────────────────────────
@Composable
fun HomeBottomNav(
    isDarkMode: Boolean,
    currentRoute: String = Routes.HOME,
    onNavigate: (String) -> Unit = {}
) {
    AppBottomNavBar(
        isDarkMode = isDarkMode,
        currentRoute = currentRoute,
        onNavigate = onNavigate
    )
}
