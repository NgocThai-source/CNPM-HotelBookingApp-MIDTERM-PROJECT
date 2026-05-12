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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.hotelbooking.app.ui.screens.home.CyanLight
import com.hotelbooking.app.ui.screens.home.CyanMain
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Review(val name: String, val date: String, val content: String, val avatarUrl: String)

@Composable
fun HotelDetailScreen(
    navController: NavController,
    hotelId: String,
    isDarkMode: Boolean = false,
    viewModel: HotelDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val detailState = viewModel.detailState

    // Gọi API lấy dữ liệu động từ Backend
    LaunchedEffect(hotelId) {
        viewModel.fetchHotelDetail(hotelId)
    }

    var userReview by remember { mutableStateOf("") }
    var isFavorite by remember { mutableStateOf(false) }

    // THÔNG TIN TĨNH: Danh sách nhận xét mẫu
    val currentReviews = remember {
        mutableStateListOf(
            Review(
                name = "Mr. Tung",
                date = "04/28/2026",
                content = "Phòng cực kỳ sạch sẽ, view đẹp sống ảo cực đỉnh. Nhân viên nhiệt tình hỗ trợ 24/7. Chắc chắn sẽ quay lại!",
                avatarUrl = "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg"
            )
        )
    }

    val bgColor = if (isDarkMode) Color(0xFF121212) else Color.White
    val surfaceColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black
    val subTextColor = if (isDarkMode) Color.LightGray else Color.Gray
    val dividerColor = if (isDarkMode) Color.DarkGray else Color.LightGray.copy(alpha = 0.5f)

    // Xử lý các trạng thái từ Backend
    when (detailState) {
        is HotelDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize().background(bgColor), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = CyanMain)
            }
        }
        is HotelDetailState.Error -> {
            Box(modifier = Modifier.fillMaxSize().background(bgColor), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Lỗi: ${detailState.message}", color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchHotelDetail(hotelId) }, colors = ButtonDefaults.buttonColors(containerColor = CyanMain)) {
                        Text("Thử lại")
                    }
                }
            }
        }
        is HotelDetailState.Success -> {
            val hotel = detailState.hotel

            Scaffold(
                bottomBar = {
                    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = surfaceColor) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = "Price per night", color = subTextColor, fontSize = 12.sp)
                                // Dữ liệu động: Giá
                                Text(text = "$${hotel.price.toInt()}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CyanMain)
                            }
                            Button(onClick = { /* TODO: Book now action */ }, colors = ButtonDefaults.buttonColors(containerColor = CyanMain), shape = RoundedCornerShape(12.dp), modifier = Modifier.height(50.dp)) {
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
                    Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                        // Dữ liệu động: Ảnh bìa
                        AsyncImage(model = hotel.imageUrl, contentDescription = hotel.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())

                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter), horizontalArrangement = Arrangement.SpaceBetween) {
                            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(surfaceColor).clickable { navController.popBackStack() }, contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = textColor)
                            }

                            Box(
                                modifier = Modifier.size(44.dp).clip(CircleShape).background(surfaceColor)
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

                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            // Dữ liệu động: Danh mục
                            Text(text = hotel.category, color = CyanMain, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                // Dữ liệu động: Đánh giá
                                Text(text = hotel.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Dữ liệu động: Tên khách sạn
                        Text(text = hotel.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textColor, lineHeight = 34.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Dữ liệu động: Địa điểm
                        Text(text = "📍 ${hotel.location}", fontSize = 14.sp, color = subTextColor)

                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = dividerColor)
                        Spacer(modifier = Modifier.height(24.dp))

                        // THÔNG TIN TĨNH: Tiện nghi
                        Text(text = "Top Amenities", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            FacilityItem(icon = Icons.Filled.Wifi, label = "Free Wifi", isDarkMode = isDarkMode)
                            FacilityItem(icon = Icons.Filled.Pool, label = "Pool", isDarkMode = isDarkMode)
                            FacilityItem(icon = Icons.Filled.FitnessCenter, label = "Gym", isDarkMode = isDarkMode)
                            FacilityItem(icon = Icons.Filled.LocalDining, label = "Restaurant", isDarkMode = isDarkMode)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = dividerColor)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Dữ liệu động: Thông tin Host
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(model = hotel.hostAvatarUrl, contentDescription = "Host", modifier = Modifier.size(50.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = "Hosted by", color = subTextColor, fontSize = 12.sp)
                                Text(text = hotel.hostName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = dividerColor)
                        Spacer(modifier = Modifier.height(24.dp))

                        // Dữ liệu động: Mô tả (About this place)
                        Text(text = "About this place", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = hotel.description.ifBlank {
                                "Experience a wonderful stay at ${hotel.title}, a perfect blend of modern design and comfort. Located in the prime area of ${hotel.location}, this property offers you a private space, an infinity pool, and the finest 5-star services."
                            },
                            color = if (isDarkMode) Color(0xFFCCCCCC) else Color.DarkGray,
                            fontSize = 15.sp,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))
                        Divider(color = dividerColor, thickness = 8.dp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // THÔNG TIN TĨNH: Khu vực nhận xét
                        Text(text = "Guest Reviews", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textColor)
                        Spacer(modifier = Modifier.height(16.dp))

                        currentReviews.forEach { review ->
                            ReviewItem(review, isDarkMode)
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = dividerColor.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        OutlinedTextField(
                            value = userReview,
                            onValueChange = { userReview = it },
                            placeholder = { Text("Share your experience...", color = subTextColor) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = if (isDarkMode) Color.Gray else Color.LightGray,
                                focusedBorderColor = CyanMain,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor,
                                cursorColor = CyanMain
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Button(
                                onClick = {
                                    if (userReview.isNotBlank()) {
                                        val date = SimpleDateFormat("MM/dd/yyyy", Locale.US).format(Date())
                                        currentReviews.add(0, Review("You", date, userReview, "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg"))
                                        userReview = ""
                                        Toast.makeText(context, "Review submitted!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanMain),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit Review", fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FacilityItem(icon: ImageVector, label: String, isDarkMode: Boolean) {
    val boxColor = if (isDarkMode) Color(0xFF1E1E1E) else CyanLight
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)).background(boxColor), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = label, tint = CyanMain, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = if (isDarkMode) Color.LightGray else Color.Gray)
    }
}

@Composable
fun ReviewItem(review: Review, isDarkMode: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = review.avatarUrl, contentDescription = null, modifier = Modifier.size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = review.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = if (isDarkMode) Color.White else Color.Black)
                Text(text = review.date, fontSize = 12.sp, color = if (isDarkMode) Color.LightGray else Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = review.content, color = if (isDarkMode) Color(0xFFCCCCCC) else Color.DarkGray, fontSize = 14.sp, lineHeight = 22.sp)
    }
}