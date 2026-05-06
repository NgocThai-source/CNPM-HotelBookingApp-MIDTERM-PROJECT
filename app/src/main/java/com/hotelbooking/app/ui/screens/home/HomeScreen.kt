package com.hotelbooking.app.ui.screens.home

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.hotelbooking.app.ui.components.BottomNavBar
import com.hotelbooking.app.ui.components.BottomNavItem
import com.hotelbooking.app.ui.components.CyanMain
import com.hotelbooking.app.ui.components.CyanLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class HotelItem(
    val imageUrl: String,
    val title: String,
    val location: String,
    val price: String,
    val rating: String,
    val category: String,
    val hostName: String,
    val badgeText: String,
    val hostAvatarUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (String) -> Unit = {}) {
    var selectedCategory by remember { mutableStateOf("All Stays") }
    var guestCount by remember { mutableStateOf(2) }
    var dateRange by remember { mutableStateOf("Oct 12 - 15") }
    var searchQuery by remember { mutableStateOf("") }

    var showGuestDialog by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    var priceRange by remember { mutableStateOf(0f..1500f) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateRangePickerState = rememberDateRangePickerState()

    val allHotelItems = remember {
        listOf(
            HotelItem("https://images.pexels.com/photos/189296/pexels-photo-189296.jpeg", "The Azure Grand Resort", "Maldives", "$450", "4.9", "Resorts", "Vinpearl Group", "Breakfast included", "https://images.pexels.com/photos/712513/pexels-photo-712513.jpeg"),
            HotelItem("https://images.pexels.com/photos/1001965/pexels-photo-1001965.jpeg", "Emerald Isle Resort", "Bora Bora", "$620", "5.0", "Resorts", "Host Tran", "Free cancellation", "https://images.pexels.com/photos/718978/pexels-photo-718978.jpeg"),
            HotelItem("https://images.pexels.com/photos/338504/pexels-photo-338504.jpeg", "Oasis Sands Resort", "Dubai", "$480", "4.8", "Resorts", "Agoda Homes", "Only 2 rooms left", "https://images.pexels.com/photos/1212984/pexels-photo-1212984.jpeg"),
            HotelItem("https://images.pexels.com/photos/1320686/pexels-photo-1320686.jpeg", "Amanpuri Hideaway", "Phuket", "$850", "4.9", "Resorts", "Aman Resorts", "Spa Voucher", "https://images.pexels.com/photos/712513/pexels-photo-712513.jpeg"),
            HotelItem("https://images.pexels.com/photos/3315291/pexels-photo-3315291.jpeg", "Four Seasons Retreat", "Bali", "$520", "4.8", "Resorts", "Mr. Michael", "Breakfast included", "https://images.pexels.com/photos/718978/pexels-photo-718978.jpeg"),
            HotelItem("https://images.pexels.com/photos/258154/pexels-photo-258154.jpeg", "Lumiere Heritage Hotel", "Paris", "$320", "4.7", "Luxury", "Accor Hotels", "Free cancellation", "https://images.pexels.com/photos/1212984/pexels-photo-1212984.jpeg"),
            HotelItem("https://images.pexels.com/photos/2034335/pexels-photo-2034335.jpeg", "Golden Coast Palace", "Miami", "$550", "4.6", "Luxury", "Hilton Group", "Ocean view", "https://images.pexels.com/photos/712513/pexels-photo-712513.jpeg"),
            HotelItem("https://images.pexels.com/photos/164595/pexels-photo-164595.jpeg", "The Ritz-Carlton Sky", "Tokyo", "$600", "4.9", "Luxury", "Ms. Jessica", "Breakfast included", "https://images.pexels.com/photos/718978/pexels-photo-718978.jpeg"),
            HotelItem("https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg", "Burj Al Arab Infinity", "Dubai", "$1200", "5.0", "Luxury", "Jumeirah", "Shuttle service", "https://images.pexels.com/photos/1212984/pexels-photo-1212984.jpeg"),
            HotelItem("https://images.pexels.com/photos/1457842/pexels-photo-1457842.jpeg", "Silver Peak Mountain Inn", "Colorado", "$280", "4.8", "Boutique", "Airbnb Pro Host", "Only 1 room left", "https://images.pexels.com/photos/712513/pexels-photo-712513.jpeg"),
            HotelItem("https://images.pexels.com/photos/1134166/pexels-photo-1134166.jpeg", "La Maison de l'Art", "Hoi An", "$150", "4.9", "Boutique", "Ms. Lan", "15% discount", "https://images.pexels.com/photos/718978/pexels-photo-718978.jpeg"),
            HotelItem("https://images.pexels.com/photos/2506990/pexels-photo-2506990.jpeg", "The Standard Vintage", "London", "$300", "4.6", "Boutique", "The Standard", "Free cancellation", "https://images.pexels.com/photos/1212984/pexels-photo-1212984.jpeg"),
            HotelItem("https://images.pexels.com/photos/1034584/pexels-photo-1034584.jpeg", "Hotel nhow Modern", "Berlin", "$220", "4.7", "Boutique", "Ms. Sarah", "Breakfast included", "https://images.pexels.com/photos/712513/pexels-photo-712513.jpeg")
        )
    }

    val displayedHotels = allHotelItems.filter { item ->
        val itemPrice = item.price.replace("$", "").toFloatOrNull() ?: 0f
        val matchCategory = selectedCategory == "All Stays" || item.category == selectedCategory
        val matchPrice = itemPrice >= priceRange.start && itemPrice <= priceRange.endInclusive
        val matchSearch = item.title.contains(searchQuery, ignoreCase = true) || item.location.contains(searchQuery, ignoreCase = true)
        matchCategory && matchPrice && matchSearch
    }.sortedBy { item -> item.price.replace("$", "").toFloatOrNull() ?: 0f }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = BottomNavItem.SEARCH.route,
                onItemClick = { item -> onNavigate(item.route) }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { HomeTopBar() }

            item {
                SearchAndFilterSection(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    dateRange = dateRange,
                    guestCount = guestCount,
                    onDateClick = { showDateDialog = true },
                    onGuestClick = { showGuestDialog = true },
                    onFilterClick = { showFilterSheet = true }
                )
            }

            item {
                CategoryChips(
                    currentCategory = selectedCategory,
                    onCategorySelected = { newCategory -> selectedCategory = newCategory }
                )
            }

            item { SectionHeader("Featured Deals", "View all") }

            if (displayedHotels.isEmpty()) {
                item {
                    Text("No hotels found matching your criteria.", color = Color.Gray, modifier = Modifier.fillMaxWidth().padding(32.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else {
                items(displayedHotels) { item ->
                    PropertyCard(item.title, item.location, item.price, item.rating, item.imageUrl, item.hostName, item.badgeText, item.hostAvatarUrl)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }, sheetState = sheetState, containerColor = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Text("Advanced Filters", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Price Range (per night)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("$${priceRange.start.roundToInt()} - $${priceRange.endInclusive.roundToInt()}", color = CyanMain, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    RangeSlider(value = priceRange, onValueChange = { priceRange = it }, valueRange = 0f..1500f, steps = 15, colors = SliderDefaults.colors(thumbColor = CyanMain, activeTrackColor = CyanMain, inactiveTrackColor = CyanLight))
                }
                Button(onClick = { showFilterSheet = false }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = CyanMain)) { Text("Show Results", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White) }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showGuestDialog) {
        AlertDialog(
            onDismissRequest = { showGuestDialog = false },
            title = { Text("Select Guests", fontWeight = FontWeight.Bold) },
            text = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Adults:", fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (guestCount > 1) guestCount-- }) { Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Decrease", tint = CyanMain) }
                        Text("$guestCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                        IconButton(onClick = { if (guestCount < 10) guestCount++ }) { Icon(Icons.Filled.AddCircleOutline, contentDescription = "Increase", tint = CyanMain) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showGuestDialog = false }) { Text("Done", color = CyanMain, fontWeight = FontWeight.Bold) } }
        )
    }

    if (showDateDialog) {
        Dialog(onDismissRequest = { showDateDialog = false }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Surface(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        DateRangePicker(state = dateRangePickerState, title = { Text("Select Booking Dates", modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp)) }, headline = { Text("Check-in - Check-out", modifier = Modifier.padding(start = 24.dp, bottom = 16.dp, end = 24.dp), fontWeight = FontWeight.Bold) }, showModeToggle = false, modifier = Modifier.fillMaxSize())
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDateDialog = false }) { Text("Cancel", color = Color.Gray) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            val startMillis = dateRangePickerState.selectedStartDateMillis
                            val endMillis = dateRangePickerState.selectedEndDateMillis
                            if (startMillis != null && endMillis != null) {
                                val formatter = SimpleDateFormat("MMM dd", Locale.US)
                                val startDate = formatter.format(Date(startMillis))
                                val endDate = formatter.format(Date(endMillis))
                                dateRange = "$startDate - $endDate"
                            }
                            showDateDialog = false
                        }, colors = ButtonDefaults.buttonColors(containerColor = CyanMain)) { Text("Confirm", fontWeight = FontWeight.Bold, color = Color.White) }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchAndFilterSection(searchQuery: String, onSearchQueryChange: (String) -> Unit, dateRange: String, guestCount: Int, onDateClick: () -> Unit, onGuestClick: () -> Unit, onFilterClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = searchQuery, onValueChange = onSearchQueryChange, placeholder = { Text("Search by name or location...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.Gray) },
            trailingIcon = { if (searchQuery.isNotEmpty()) { IconButton(onClick = { onSearchQueryChange("") }) { Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = Color.Gray) } } },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray, unfocusedContainerColor = Color.White)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoBox(icon = Icons.Filled.DateRange, title = "DATES", value = dateRange, modifier = Modifier.weight(1f).clickable { onDateClick() })
            InfoBox(icon = Icons.Filled.Person, title = "GUESTS", value = "$guestCount Adults", modifier = Modifier.weight(1f).clickable { onGuestClick() })
            Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(CyanMain).clickable { onFilterClick() }, contentAlignment = Alignment.Center) { Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = Color.White) }
        }
    }
}

@Composable
fun InfoBox(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.height(50.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = CyanMain, modifier = Modifier.size(20.dp))
        Column { Text(title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold); Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black) }
    }
}

@Composable
fun CategoryChips(currentCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("All Stays", "Luxury", "Resorts", "Boutique")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories.size) { index ->
            val cat = categories[index]
            val isSelected = cat == currentCategory
            Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (isSelected) CyanMain else Color.White).clickable { onCategorySelected(cat) }.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(cat, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun HomeTopBar() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color.Black), contentAlignment = Alignment.Center) { Icon(Icons.Filled.HomeWork, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(24.dp)) }
        Text("Explore Stays", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        IconButton(onClick = { }) { Icon(Icons.Filled.MoreHoriz, contentDescription = "Menu") }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black); Text(actionText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanMain)
    }
}

@Composable
fun PropertyCard(name: String, location: String, price: String, rating: String, imageUrl: String, hostName: String, badgeText: String, hostAvatarUrl: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column {
            AsyncImage(model = imageUrl, contentDescription = name, modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)), contentScale = ContentScale.Crop)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(36.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) { Icon(Icons.Filled.FavoriteBorder, contentDescription = "Favorite", tint = Color.Gray, modifier = Modifier.size(20.dp)) }
                Row(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp).clip(RoundedCornerShape(8.dp)).background(Color.White).padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = "Star", tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(rating, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black); Text(price, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CyanMain)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.LocationOn, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(14.dp)); Spacer(modifier = Modifier.width(4.dp)); Text(location, fontSize = 12.sp, color = Color.Gray) }
                    Text("PER NIGHT", fontSize = 10.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(model = hostAvatarUrl, contentDescription = "Host Avatar", modifier = Modifier.size(24.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(hostName, fontSize = 12.sp, color = Color.Gray)
                    }
                    if (badgeText.isNotEmpty()) {
                        Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(CyanLight).padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(badgeText, fontSize = 10.sp, color = CyanMain, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}