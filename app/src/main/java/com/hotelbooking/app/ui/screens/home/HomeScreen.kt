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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

val CyanMain = Color(0xFF00E5FF)
val CyanLight = Color(0xFFE0F7FA)

data class HotelItem(
    val imageUrl: String,
    val title: String,
    val location: String,
    val price: String,
    val rating: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // 1. CÁC BIẾN TRẠNG THÁI (STATE)
    var selectedCategory by remember { mutableStateOf("All Stays") }
    var guestCount by remember { mutableStateOf(2) }
    var dateRange by remember { mutableStateOf("Oct 12 - 15") }
    var searchQuery by remember { mutableStateOf("") } // MỚI: Biến lưu trữ từ khóa tìm kiếm

    var showGuestDialog by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    var priceRange by remember { mutableStateOf(0f..1500f) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateRangePickerState = rememberDateRangePickerState()

    // 2. DANH SÁCH DỮ LIỆU
    val allHotelItems = remember {
        listOf(
            HotelItem("https://images.pexels.com/photos/189296/pexels-photo-189296.jpeg", "The Azure Grand Resort", "Maldives", "$450", "4.9", "Resorts"),
            HotelItem("https://images.pexels.com/photos/1001965/pexels-photo-1001965.jpeg", "Emerald Isle Resort", "Bora Bora", "$620", "5.0", "Resorts"),
            HotelItem("https://images.pexels.com/photos/338504/pexels-photo-338504.jpeg", "Oasis Sands Resort", "Dubai", "$480", "4.8", "Resorts"),
            HotelItem("https://images.pexels.com/photos/1320686/pexels-photo-1320686.jpeg", "Amanpuri Hideaway", "Phuket, Thailand", "$850", "4.9", "Resorts"),
            HotelItem("https://images.pexels.com/photos/3315291/pexels-photo-3315291.jpeg", "Four Seasons Retreat", "Bali, Indonesia", "$520", "4.8", "Resorts"),
            HotelItem("https://images.pexels.com/photos/258154/pexels-photo-258154.jpeg", "Lumiere Heritage Hotel", "Paris, France", "$320", "4.7", "Hotel"),
            HotelItem("https://images.pexels.com/photos/2034335/pexels-photo-2034335.jpeg", "Golden Coast Palace", "Miami, USA", "$550", "4.6", "Hotel"),
            HotelItem("https://images.pexels.com/photos/164595/pexels-photo-164595.jpeg", "The Ritz-Carlton Sky", "Tokyo, Japan", "$600", "4.9", "Hotel"),
            HotelItem("https://images.pexels.com/photos/323780/pexels-photo-323780.jpeg", "Burj Al Arab Infinity", "Dubai, UAE", "$1200", "5.0", "Hotel"),
            HotelItem("https://images.pexels.com/photos/1457842/pexels-photo-1457842.jpeg", "Silver Peak Mountain Inn", "Colorado, USA", "$280", "4.8", "Boutique"),
            HotelItem("https://images.pexels.com/photos/1134166/pexels-photo-1134166.jpeg", "La Maison de l'Art", "Hoi An, Vietnam", "$150", "4.9", "Boutique"),
            HotelItem("https://images.pexels.com/photos/2506990/pexels-photo-2506990.jpeg", "The Standard Vintage", "London, UK", "$300", "4.6", "Boutique"),
            HotelItem("https://images.pexels.com/photos/1034584/pexels-photo-1034584.jpeg", "Hotel nhow Modern", "Berlin, Germany", "$220", "4.7", "Boutique")
        )
    }

    // 3. LOGIC LỌC TỔNG HỢP (ĐIỀU KIỆN TÌM KIẾM)
    val displayedHotels = allHotelItems.filter { item ->
        val itemPrice = item.price.replace("$", "").toFloatOrNull() ?: 0f

        // Kiểm tra Danh mục
        val matchCategory = selectedCategory == "All Stays" || item.category == selectedCategory
        // Kiểm tra Giá
        val matchPrice = itemPrice >= priceRange.start && itemPrice <= priceRange.endInclusive
        // Kiểm tra Từ khóa tìm kiếm (so sánh cả tên và địa điểm, không phân biệt hoa/thường)
        val matchSearch = item.title.contains(searchQuery, ignoreCase = true) ||
                item.location.contains(searchQuery, ignoreCase = true)

        matchCategory && matchPrice && matchSearch // Phải thỏa mãn cả 3 điều kiện
    }.sortedBy { item ->
        item.price.replace("$", "").toFloatOrNull() ?: 0f
    }

    // 4. GIAO DIỆN CHÍNH
    Scaffold(
        bottomBar = { HomeBottomNav() },
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
                    searchQuery = searchQuery, // Truyền biến tìm kiếm vào
                    onSearchQueryChange = { searchQuery = it }, // Bắt sự kiện người dùng gõ
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

            // Nếu tìm không thấy khách sạn nào, hiện thông báo
            if (displayedHotels.isEmpty()) {
                item {
                    Text(
                        text = "Không tìm thấy khách sạn nào phù hợp.",
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                items(displayedHotels) { item ->
                    PropertyCard(item.title, item.location, item.price, item.rating, item.imageUrl)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // --- CÁC HỘP THOẠI ĐIỀU KHIỂN ---

    // HỘP THOẠI BỘ LỌC TÌM KIẾM
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text("Bộ lọc nâng cao", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Khoảng giá (1 đêm)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("$${priceRange.start.roundToInt()} - $${priceRange.endInclusive.roundToInt()}", color = CyanMain, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    RangeSlider(
                        value = priceRange,
                        onValueChange = { priceRange = it },
                        valueRange = 0f..1500f,
                        steps = 15,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanMain,
                            activeTrackColor = CyanMain,
                            inactiveTrackColor = CyanLight
                        )
                    )
                }
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanMain)
                ) {
                    Text("Hiển thị kết quả", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // HỘP THOẠI CHỌN KHÁCH
    if (showGuestDialog) {
        AlertDialog(
            onDismissRequest = { showGuestDialog = false },
            title = { Text("Chọn số lượng khách", fontWeight = FontWeight.Bold) },
            text = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Số người lớn:", fontSize = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (guestCount > 1) guestCount-- }) { Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Giảm", tint = CyanMain) }
                        Text("$guestCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                        IconButton(onClick = { if (guestCount < 10) guestCount++ }) { Icon(Icons.Filled.AddCircleOutline, contentDescription = "Tăng", tint = CyanMain) }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showGuestDialog = false }) { Text("Xong", color = CyanMain, fontWeight = FontWeight.Bold) } }
        )
    }

    // CUỐN LỊCH ẢO
    if (showDateDialog) {
        Dialog(
            onDismissRequest = { showDateDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxWidth(0.95f).fillMaxHeight(0.85f), shape = RoundedCornerShape(16.dp), color = Color.White) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        DateRangePicker(
                            state = dateRangePickerState,
                            title = { Text("Chọn ngày đặt phòng", modifier = Modifier.padding(start = 24.dp, top = 16.dp, end = 24.dp)) },
                            headline = { Text("Ngày nhận - Ngày trả", modifier = Modifier.padding(start = 24.dp, bottom = 16.dp, end = 24.dp), fontWeight = FontWeight.Bold) },
                            showModeToggle = false, modifier = Modifier.fillMaxSize()
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDateDialog = false }) { Text("Hủy", color = Color.Gray) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val startMillis = dateRangePickerState.selectedStartDateMillis
                                val endMillis = dateRangePickerState.selectedEndDateMillis
                                if (startMillis != null && endMillis != null) {
                                    val formatter = SimpleDateFormat("MMM dd", Locale.US)
                                    val startDate = formatter.format(Date(startMillis))
                                    val endDate = formatter.format(Date(endMillis))
                                    dateRange = "$startDate - $endDate"
                                }
                                showDateDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanMain)
                        ) { Text("Xác nhận", fontWeight = FontWeight.Bold, color = Color.White) }
                    }
                }
            }
        }
    }
}

// --- CÁC COMPOSABLE GIAO DIỆN ---

// Cập nhật hàm này để nhận và gửi query tìm kiếm
@Composable
fun SearchAndFilterSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    dateRange: String,
    guestCount: Int,
    onDateClick: () -> Unit,
    onGuestClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = searchQuery, // Hiển thị nội dung đang gõ
            onValueChange = onSearchQueryChange, // Cập nhật nội dung gõ
            placeholder = { Text("Tìm kiếm tên hoặc địa điểm...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.Gray) },
            trailingIcon = {
                // nút X để xóa nhanh từ khóa tìm kiếm
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = Color.Gray)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray, unfocusedContainerColor = Color.White)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoBox(icon = Icons.Filled.DateRange, title = "DATES", value = dateRange, modifier = Modifier.weight(1f).clickable { onDateClick() })
            InfoBox(icon = Icons.Filled.Person, title = "GUESTS", value = "$guestCount Adults", modifier = Modifier.weight(1f).clickable { onGuestClick() })
            Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(CyanMain).clickable { onFilterClick() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = Color.White)
            }
        }
    }
}

@Composable
fun InfoBox(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.height(50.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(icon, contentDescription = null, tint = CyanMain, modifier = Modifier.size(20.dp))
        Column {
            Text(title, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun CategoryChips(currentCategory: String, onCategorySelected: (String) -> Unit) {
    val categories = listOf("All Stays", "Hotel", "Resorts", "Boutique")
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
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(actionText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CyanMain)
    }
}

@Composable
fun PropertyCard(name: String, location: String, price: String, rating: String, imageUrl: String) {
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
                    Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(price, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CyanMain)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.LocationOn, contentDescription = "Location", tint = Color.Gray, modifier = Modifier.size(14.dp)); Spacer(modifier = Modifier.width(4.dp)); Text(location, fontSize = 12.sp, color = Color.Gray) }
                    Text("PER NIGHT", fontSize = 10.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.LightGray)); Spacer(modifier = Modifier.width(8.dp)); Text("Hosted by Premium Partners", fontSize = 12.sp, color = Color.Gray) }
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(CyanLight).padding(horizontal = 8.dp, vertical = 4.dp)) { Text("Exclusive", fontSize = 10.sp, color = CyanMain, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
fun HomeBottomNav() {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(icon = { Icon(Icons.Filled.Search, contentDescription = "Search") }, label = { Text("Search") }, selected = true, onClick = { }, colors = NavigationBarItemDefaults.colors(selectedIconColor = CyanMain, selectedTextColor = CyanMain, indicatorColor = CyanLight))
        NavigationBarItem(icon = { Icon(Icons.Filled.DateRange, contentDescription = "Bookings") }, label = { Text("Bookings") }, selected = false, onClick = { })
        NavigationBarItem(icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat") }, label = { Text("Chat") }, selected = false, onClick = { })
        NavigationBarItem(icon = { Icon(Icons.Filled.VerifiedUser, contentDescription = "Admin") }, label = { Text("Admin") }, selected = false, onClick = { })
        NavigationBarItem(icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") }, label = { Text("Settings") }, selected = false, onClick = { })
    }
}