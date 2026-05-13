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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// LƯU Ý: NẾU BẠN CÓ FILE AppColors và HotelDetailState, hãy import chúng vào đây
// import com.hotelbooking.app.ui.theme.AppColors
// import com.hotelbooking.app.ui.screens.detail.HotelDetailState

data class Review(val name: String, val date: String, val content: String, val avatarUrl: String)

@Composable
fun HotelDetailScreen(
    navController: NavController,
    hotelId: String, // Đổi từ hotelName sang hotelId để gọi API
    viewModel: Any,  // ĐỔI 'Any' THÀNH TÊN VIEWMODEL CỦA BẠN (VD: HotelDetailViewModel)
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current

    // Ép kiểu hoặc thay thế bằng biến state thực tế từ ViewModel của bạn
    // val detailState = viewModel.detailState

    // Gọi API khi vào màn hình
    // LaunchedEffect(hotelId) { viewModel.fetchHotelDetail(hotelId) }

    var userReview by remember { mutableStateOf("") }
    val currentReviews = remember {
        mutableStateListOf(
            Review(
                name = "Mr. Tung",
                date = "04/28/2026",
                content = "Extremely clean room, beautiful view for amazing photos. Enthusiastic staff supporting 24/7. Will definitely come back!",
                avatarUrl = "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg"
            )
        )
    }

    // --- MÀU SẮC DỰ PHÒNG (Nếu bạn dùng AppColors thì đổi lại nhé) ---
    val bgColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val surfaceColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val subTextColor = if (isDarkMode) Color.LightGray else Color.Gray
    val dividerColor = if (isDarkMode) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f)
    val cyanMain = Color(0xFF00BCD4)

    // TODO: BẠN SẼ BỌC TRONG when (detailState) Ở ĐÂY KHI NỐI API, TẠM THỜI MÌNH HIỂN THỊ GIAO DIỆN CHÍNH

    var isFavorite by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = surfaceColor) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "Price per night", color = subTextColor, fontSize = 12.sp)
                        Text(text = "$150", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = cyanMain) // Thay bằng giá thật
                    }
                    Button(
                        onClick = { /* TODO: Book now action */ },
                        colors = ButtonDefaults.buttonColors(containerColor = cyanMain),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text(text = "Book Now", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(bgColor)
        ) {
            // Hero Image
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)) {
                // Thay URL ảnh của bạn vào đây
                AsyncImage(
                    model = "https://images.pexels.com/photos/164595/pexels-photo-164595.jpeg",
                    contentDescription = "Hotel Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(colors = listOf(Color.Transparent, bgColor))))

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                    .align(Alignment.TopCenter), horizontalArrangement = Arrangement.SpaceBetween) {

                    // Nút Back
                    Box(modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(surfaceColor.copy(alpha = 0.8f))
                        .clickable { navController.popBackStack() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                    }

                    // Nút Tim
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(surfaceColor.copy(alpha = 0.8f))
                            .clickable {
                                isFavorite = !isFavorite
                                val msg = if (isFavorite) "Saved to Favorites 💖" else "Removed from Favorites"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else textColor
                        )
                    }
                }
            }

            // Phần thông tin chi tiết
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Surface(shape = RoundedCornerShape(8.dp), color = cyanMain.copy(alpha = 0.12f)) {
                        Text(text = "Luxury Resort", color = cyanMain, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "4.8", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Grand Hotel Paradise", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = textColor, lineHeight = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = subTextColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Đà Nẵng, Việt Nam", fontSize = 14.sp, color = subTextColor)
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = dividerColor)
                Spacer(modifier = Modifier.height(24.dp))

                // Tiện ích
                Text(text = "Top Amenities", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    FacilityItem(icon = Icons.Filled.Wifi, label = "Free Wifi", isDarkMode = isDarkMode)
                    FacilityItem(icon = Icons.Filled.Pool, label = "Pool", isDarkMode = isDarkMode)
                    FacilityItem(icon = Icons.Filled.FitnessCenter, label = "Gym", isDarkMode = isDarkMode)
                    FacilityItem(icon = Icons.Filled.LocalDining, label = "Restaurant", isDarkMode = isDarkMode)
                }

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = dividerColor, thickness = 6.dp)
                Spacer(modifier = Modifier.height(24.dp))

                // Khu vực Nhận xét
                Text(text = "Guest Reviews", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                Spacer(modifier = Modifier.height(16.dp))

                currentReviews.forEach { review ->
                    ReviewItem(review = review, isDarkMode = isDarkMode)
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = dividerColor.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = userReview, onValueChange = { userReview = it },
                    placeholder = { Text("Share your experience...", color = subTextColor, fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = dividerColor, focusedBorderColor = cyanMain,
                        focusedTextColor = textColor, unfocusedTextColor = textColor,
                        cursorColor = cyanMain
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = {
                        if (userReview.isNotBlank()) {
                            val date = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date())
                            currentReviews.add(0, Review("You", date, userReview, "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg"))
                            userReview = ""
                            Toast.makeText(context, "Review submitted!", Toast.LENGTH_SHORT).show()
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = cyanMain), shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Review", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FacilityItem(icon: ImageVector, label: String, isDarkMode: Boolean) {
    val boxColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFE0F7FA)
    val textColor = if (isDarkMode) Color.LightGray else Color.Gray
    val cyanMain = Color(0xFF00BCD4)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(60.dp), shape = RoundedCornerShape(18.dp), color = boxColor) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = cyanMain, modifier = Modifier.size(26.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ReviewItem(review: Review, isDarkMode: Boolean) {
    val textColor = if (isDarkMode) Color.White else Color.Black
    val subTextColor = if (isDarkMode) Color.LightGray else Color.Gray
    val contentColor = if (isDarkMode) Color(0xFFCCCCCC) else Color.DarkGray

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = review.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = review.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textColor)
                Text(text = review.date, fontSize = 12.sp, color = subTextColor)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = review.content, color = contentColor, fontSize = 14.sp, lineHeight = 22.sp)
    }
}