package com.hotelbooking.app.ui.screens.home

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

// Keep backward-compatible color references
val CyanMain = AppColors.CyanMain
val CyanLight = AppColors.CyanSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, isDarkMode: Boolean, onThemeToggle: () -> Unit) {
    val context = LocalContext.current
    Log.d("HomeScreen", ">>> HomeScreen composable started")
    val viewModel: HomeViewModel = viewModel()
    val hotels by viewModel.hotels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val realtimeUpdated by viewModel.realtimeUpdated.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    Log.d("HomeScreen", ">>> State: isLoading=$isLoading, error=$error, hotels count=${hotels.size}")

    // Show error as Toast so user can report it
    LaunchedEffect(error) {
        if (error != null) {
            Log.e("HomeScreen", ">>> ERROR state: $error")
            Toast.makeText(
                context,
                "API Error: $error",
                Toast.LENGTH_LONG
            ).show()
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
    var favorites by remember { mutableStateOf(setOf<String>()) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateRangePickerState = rememberDateRangePickerState()

    val displayedHotels = hotels.filter { item ->
        val matchCategory = selectedCategory == "All Stays" || item.category == selectedCategory ||
            (selectedCategory == "Resorts" && item.category == "Resort") ||
            (selectedCategory == "Boutique" && item.category == "Boutique") ||
            (selectedCategory == "Luxury" && item.category == "Luxury")
        val matchPrice = item.price >= priceRange.start && item.price <= priceRange.endInclusive
        val matchSearch = item.title.contains(searchQuery, ignoreCase = true) || item.location.contains(searchQuery, ignoreCase = true)
        matchCategory && matchPrice && matchSearch
    }.sortedBy { it.price }




    Scaffold(
        bottomBar = { HomeBottomNav(isDarkMode, "home") { route -> navController.navigate(route) } },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = AppColors.background(isDarkMode)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item { HomeTopBar(isDarkMode, onThemeToggle, { viewModel.refreshAll() }, isLoading) }
            item {
                SearchAndFilterSection(
                    query = searchQuery, onQueryChange = { searchQuery = it },
                    dateRange = dateRange, guestCount = guestCount,
                    onDateClick = { showDateDialog = true }, onGuestClick = { showGuestDialog = true },
                    onFilterClick = { showFilterSheet = true }, isDarkMode = isDarkMode
                )
            }
            item { CategoryChips(currentCategory = selectedCategory, onCategorySelected = { selectedCategory = it }, isDarkMode = isDarkMode) }
            item { SectionHeader("Available Hotels", "View all", isDarkMode) }

            // Loading state
            if (isLoading) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = AppColors.CyanMain, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading hotels...", color = AppColors.textSecondary(isDarkMode), fontSize = 14.sp)
                    }
                }
            } else if (error != null && hotels.isEmpty()) {
                // Error state
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.CloudOff, contentDescription = null, modifier = Modifier.size(48.dp), tint = AppColors.textTertiary(isDarkMode))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Could not load hotels", color = AppColors.textSecondary(isDarkMode), fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.fetchHotels() }) {
                            Text("Retry", color = AppColors.CyanMain, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (displayedHotels.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.SearchOff, contentDescription = null, modifier = Modifier.size(48.dp), tint = AppColors.textTertiary(isDarkMode))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No matching hotels found.", color = AppColors.textSecondary(isDarkMode), fontSize = 15.sp)
                    }
                }
            } else {
                items(displayedHotels) { hotel ->
                    PropertyCard(
                        hotel = hotel,
                        isFavorite = favorites.contains(hotel.id),
                        onFavoriteToggle = {
                            favorites = if (favorites.contains(hotel.id)) {
                                favorites - hotel.id
                            } else {
                                favorites + hotel.id
                            }
                        },
                        isDarkMode = isDarkMode,
                        onClick = { navController.navigate(Routes.DETAIL.replace("{hotelId}", hotel.id)) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false }, sheetState = sheetState,
            containerColor = AppColors.surface(isDarkMode),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Text("Advanced Filters", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary(isDarkMode))
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Price Range (Per Night)", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = AppColors.textPrimary(isDarkMode))
                        Text("$${priceRange.start.roundToInt()} - $${priceRange.endInclusive.roundToInt()}", color = AppColors.CyanMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    RangeSlider(value = priceRange, onValueChange = { priceRange = it }, valueRange = 0f..500000f, steps = 25,
                        colors = SliderDefaults.colors(thumbColor = AppColors.CyanMain, activeTrackColor = AppColors.CyanMain, inactiveTrackColor = AppColors.border(isDarkMode)))
                }
                Button(onClick = { showFilterSheet = false }, modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = AppColors.CyanMain),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) { Text("Show Results", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White) }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Guest Dialog
    if (showGuestDialog) {
        AlertDialog(
            onDismissRequest = { showGuestDialog = false },
            containerColor = AppColors.surface(isDarkMode),
            title = { Text("Select Guests", fontWeight = FontWeight.Bold, color = AppColors.textPrimary(isDarkMode)) },
            text = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Adults:", fontSize = 16.sp, color = AppColors.textPrimary(isDarkMode))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (guestCount > 1) guestCount-- }) { Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Decrease", tint = AppColors.CyanMain) }
                        Text("$guestCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp), color = AppColors.textPrimary(isDarkMode))
                        IconButton(onClick = { if (guestCount < 10) guestCount++ }) { Icon(Icons.Filled.AddCircleOutline, contentDescription = "Increase", tint = AppColors.CyanMain) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showGuestDialog = false }) { Text("Done", color = AppColors.CyanMain, fontWeight = FontWeight.Bold) } }
        )
    }

    // Date Picker Dialog
    if (showDateDialog) {
        Dialog(onDismissRequest = { showDateDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), shape = RoundedCornerShape(20.dp), color = AppColors.surface(isDarkMode)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        DateRangePicker(state = dateRangePickerState,
                            title = { Text("Select Dates", modifier = Modifier.padding(start = 24.dp, top = 16.dp), color = AppColors.textPrimary(isDarkMode)) },
                            headline = { Text("Check-in - Check-out", modifier = Modifier.padding(start = 24.dp), fontWeight = FontWeight.Bold, color = AppColors.textPrimary(isDarkMode)) },
                            showModeToggle = false, modifier = Modifier.fillMaxSize())
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDateDialog = false }) { Text("Cancel", color = AppColors.textSecondary(isDarkMode)) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            val startMillis = dateRangePickerState.selectedStartDateMillis
                            val endMillis = dateRangePickerState.selectedEndDateMillis
                            if (startMillis != null && endMillis != null) {
                                val formatter = SimpleDateFormat("MMM dd", Locale.US)
                                dateRange = "${formatter.format(java.util.Date(startMillis))} - ${formatter.format(java.util.Date(endMillis))}"
                            }
                            showDateDialog = false
                        }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.CyanMain), shape = RoundedCornerShape(12.dp)
                        ) { Text("Confirm", fontWeight = FontWeight.Bold, color = Color.White) }
                    }
                }
            }
        }
    }
}

// ── Top Bar with Logo ──
@Composable
fun HomeTopBar(
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    onReloadClick: () -> Unit,
    isLoading: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Text("Discover Hotels", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary(isDarkMode))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Reload button
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(26.dp).padding(end = 4.dp),
                    strokeWidth = 2.dp,
                    color = AppColors.CyanMain
                )
            } else {
                IconButton(onClick = onReloadClick) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reload hotels",
                        tint = if (isDarkMode) AppColors.Gold else AppColors.TextMedium,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            // Theme toggle
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Filled.WbSunny else Icons.Filled.NightsStay,
                    contentDescription = "Toggle Theme",
                    tint = if (isDarkMode) AppColors.Gold else AppColors.TextMedium,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

// ── Search & Filter ──
@Composable
fun SearchAndFilterSection(query: String, onQueryChange: (String) -> Unit, dateRange: String, guestCount: Int, onDateClick: () -> Unit, onGuestClick: () -> Unit, onFilterClick: () -> Unit, isDarkMode: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = query, onValueChange = onQueryChange,
            placeholder = { Text("Search hotels or locations...", color = AppColors.textTertiary(isDarkMode), fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = AppColors.textTertiary(isDarkMode), modifier = Modifier.size(20.dp)) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = AppColors.border(isDarkMode), focusedBorderColor = AppColors.CyanMain,
                unfocusedContainerColor = AppColors.card(isDarkMode), focusedContainerColor = AppColors.card(isDarkMode),
                focusedTextColor = AppColors.textPrimary(isDarkMode), unfocusedTextColor = AppColors.textPrimary(isDarkMode)
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoBox(icon = Icons.Filled.DateRange, title = "DATES", value = dateRange, modifier = Modifier.weight(1f).clickable { onDateClick() }, isDarkMode = isDarkMode)
            InfoBox(icon = Icons.Filled.Person, title = "GUESTS", value = "$guestCount Adults", modifier = Modifier.weight(1f).clickable { onGuestClick() }, isDarkMode = isDarkMode)
            Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)).background(AppColors.CyanMain).clickable { onFilterClick() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    }
}

// ── Info Box ──
@Composable
fun InfoBox(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, modifier: Modifier = Modifier, isDarkMode: Boolean) {
    Row(modifier = modifier.height(50.dp).clip(RoundedCornerShape(14.dp)).background(AppColors.card(isDarkMode)).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = AppColors.CyanMain, modifier = Modifier.size(18.dp))
        Column {
            Text(title, fontSize = 9.sp, color = AppColors.textTertiary(isDarkMode), fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppColors.textPrimary(isDarkMode))
        }
    }
}

// ── Category Chips ──
@Composable
fun CategoryChips(currentCategory: String, onCategorySelected: (String) -> Unit, isDarkMode: Boolean) {
    val categories = listOf("All Stays", "Luxury", "Resort", "Boutique", "Hotel", "Homestay", "Villa")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(categories) { cat ->
            val isSelected = cat == currentCategory
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) AppColors.CyanMain else AppColors.card(isDarkMode),
                animationSpec = tween(250), label = "chip_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else AppColors.textSecondary(isDarkMode),
                animationSpec = tween(250), label = "chip_text"
            )
            Surface(
                modifier = Modifier.clickable { onCategorySelected(cat) },
                shape = RoundedCornerShape(24.dp), color = bgColor,
                shadowElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Text(cat, color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp))
            }
        }
    }
}

// ── Section Header ──
@Composable
fun SectionHeader(title: String, actionText: String, isDarkMode: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary(isDarkMode))
        Text(actionText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AppColors.CyanMain)
    }
}

// ── Property Card (now uses Hotel data class from API) ──
@Composable
fun PropertyCard(hotel: Hotel, isFavorite: Boolean, onFavoriteToggle: () -> Unit, isDarkMode: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current

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

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.card(isDarkMode)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkMode) 0.dp else 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                AsyncImage(model = hotel.imageUrl, contentDescription = hotel.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                // Gradient overlay at bottom
                Box(modifier = Modifier.fillMaxWidth().height(60.dp).align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)))))
                // Favorite button
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(38.dp).clip(CircleShape)
                    .background(if (isDarkMode) AppColors.DarkCard.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.9f))
                    .clickable {
                        onFavoriteToggle()
                        Toast.makeText(context, if (!isFavorite) "Saved!" else "Removed", Toast.LENGTH_SHORT).show()
                    }, contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null, tint = if (isFavorite) AppColors.Error else AppColors.textSecondary(isDarkMode), modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(hotel.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary(isDarkMode),
                        maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$${hotel.price.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.CyanMain)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = AppColors.textTertiary(isDarkMode), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(hotel.location, fontSize = 12.sp, color = AppColors.textSecondary(isDarkMode))
                }
                // Check-in / Check-out dates
                if (!hotel.checkInDate.isNullOrEmpty() || !hotel.checkOutDate.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (!hotel.checkInDate.isNullOrEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.FlightLand, contentDescription = null, tint = AppColors.CyanMain, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("In: ${hotel.checkInDate}", fontSize = 11.sp, color = AppColors.textSecondary(isDarkMode))
                            }
                        }
                        if (!hotel.checkOutDate.isNullOrEmpty()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.FlightTakeoff, contentDescription = null, tint = AppColors.CyanMain, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("Out: ${hotel.checkOutDate}", fontSize = 11.sp, color = AppColors.textSecondary(isDarkMode))
                            }
                        }
                    }
                }
                // Amenities
                if (!hotel.amenities.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                        hotel.amenities.take(4).forEach { amenity ->
                            val icon = amenityIcons[amenity.lowercase()]
                            if (icon != null) {
                                Surface(shape = RoundedCornerShape(6.dp), color = AppColors.CyanSurface) {
                                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Icon(icon, contentDescription = amenity, tint = AppColors.CyanMain, modifier = Modifier.size(12.dp))
                                        Text(amenity.replaceFirstChar { it.uppercase() }, fontSize = 10.sp, color = AppColors.CyanMain, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!hotel.hostAvatarUrl.isNullOrEmpty()) {
                            AsyncImage(model = hotel.hostAvatarUrl, contentDescription = null, modifier = Modifier.size(24.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(hotel.hostName.orEmpty().ifEmpty { "Host" }, fontSize = 12.sp, color = AppColors.textSecondary(isDarkMode))
                    }
                    if (!hotel.badgeText.isNullOrEmpty()) {
                        Surface(shape = RoundedCornerShape(8.dp), color = AppColors.CyanMain.copy(alpha = 0.12f)) {
                            Text(hotel.badgeText, fontSize = 10.sp, color = AppColors.CyanMain, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Bottom Navigation ──
@Composable
fun HomeBottomNav(
    isDarkMode: Boolean,
    currentRoute: String = "home",
    onNavigate: (String) -> Unit = {}
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
            selected = currentRoute == Routes.MY_BOOKINGS,
            onClick = { onNavigate(Routes.MY_BOOKINGS) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AppColors.CyanMain,
                selectedTextColor = AppColors.CyanMain,
                indicatorColor = AppColors.CyanMain.copy(alpha = 0.12f),
                unselectedIconColor = AppColors.textTertiary(isDarkMode),
                unselectedTextColor = AppColors.textTertiary(isDarkMode)
            )
        )
        NavigationBarItem(icon = { Icon(Icons.Filled.Chat, contentDescription = null) }, label = { Text("Chat", fontSize = 11.sp) }, selected = false, onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = AppColors.textTertiary(isDarkMode), unselectedTextColor = AppColors.textTertiary(isDarkMode)))
        NavigationBarItem(icon = { Icon(Icons.Filled.Settings, contentDescription = null) }, label = { Text("Settings", fontSize = 11.sp) }, selected = false, onClick = { },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = AppColors.textTertiary(isDarkMode), unselectedTextColor = AppColors.textTertiary(isDarkMode)))
    }
}