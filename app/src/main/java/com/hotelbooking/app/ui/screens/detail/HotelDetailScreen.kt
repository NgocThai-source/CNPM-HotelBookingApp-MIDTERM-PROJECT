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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hotelbooking.app.ui.screens.home.CyanLight
import com.hotelbooking.app.ui.screens.home.CyanMain
import com.hotelbooking.app.ui.screens.home.hotelList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Review(val name: String, val date: String, val content: String, val avatarUrl: String)

@Composable
fun HotelDetailScreen(navController: NavController, hotelName: String) {
    val hotel = hotelList.find { it.title == hotelName }
    val context = LocalContext.current

    var userReview by remember { mutableStateOf("") }

    val currentReviews = remember {
        mutableStateListOf(
            Review(
                name = "Anh Tùng",
                date = "28/04/2026",
                content = "Phòng cực kỳ sạch sẽ, view chụp ảnh sống ảo rất đẹp. Nhân viên nhiệt tình hỗ trợ 24/7. Nhất định sẽ quay lại!",
                avatarUrl = "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg"
            )
        )
    }

    if (hotel == null) {
        navController.popBackStack()
        return
    }

    Scaffold(
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp, color = Color.White) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(text = "Giá mỗi đêm", color = Color.Gray, fontSize = 12.sp)
                        Text(text = hotel.price, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = CyanMain)
                    }
                    Button(onClick = { /* TODO: Đặt phòng */ }, colors = ButtonDefaults.buttonColors(containerColor = CyanMain), shape = RoundedCornerShape(12.dp), modifier = Modifier.height(50.dp)) {
                        Text(text = "Đặt phòng ngay", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White, maxLines = 1)
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
                .background(Color.White)
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                AsyncImage(model = hotel.imageUrl, contentDescription = hotel.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 16.dp, end = 16.dp).align(Alignment.TopCenter), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White).clickable { navController.popBackStack() }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }

                    Box(
                        modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White)
                            .clickable {
                                hotel.isFavorite.value = !hotel.isFavorite.value
                                val msg = if (hotel.isFavorite.value) "Đã lưu vào danh sách Yêu thích 💖" else "Đã bỏ Yêu thích"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (hotel.isFavorite.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (hotel.isFavorite.value) Color.Red else Color.Black
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(text = hotel.category, color = CyanMain, fontWeight = FontWeight.Bold, fontSize = 14.sp); Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(4.dp)); Text(text = hotel.rating, fontWeight = FontWeight.Bold, fontSize = 16.sp) } }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = hotel.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 34.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "📍 ${hotel.location}", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Tiện nghi nổi bật", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { FacilityItem(icon = Icons.Filled.Wifi, label = "Free Wifi"); FacilityItem(icon = Icons.Filled.Pool, label = "Hồ bơi"); FacilityItem(icon = Icons.Filled.FitnessCenter, label = "Phòng Gym"); FacilityItem(icon = Icons.Filled.LocalDining, label = "Nhà hàng") }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) { AsyncImage(model = hotel.hostAvatarUrl, contentDescription = "Host", modifier = Modifier.size(50.dp).clip(CircleShape), contentScale = ContentScale.Crop); Spacer(modifier = Modifier.width(16.dp)); Column { Text(text = "Được quản lý bởi", color = Color.Gray, fontSize = 12.sp); Text(text = hotel.hostName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black) } }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Giới thiệu", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Trải nghiệm kỳ nghỉ tuyệt vời tại ${hotel.title}, nơi kết hợp hoàn hảo giữa thiết kế hiện đại và sự tiện nghi. Tọa lạc tại khu vực đắc địa của ${hotel.location}, chỗ nghỉ này cung cấp cho bạn không gian riêng tư, hồ bơi vô cực và các dịch vụ chuẩn 5 sao cao cấp nhất. \n\nMỗi phòng đều được trang bị giường king-size với nệm êm ái, ban công rộng view nhìn toàn cảnh thành phố và khu vực minibar miễn phí. Nơi đây thực sự là thiên đường lý tưởng để bạn gác lại mọi âu lo, tận hưởng khoảng thời gian thư giãn trọn vẹn bên những người thân yêu.",
                    color = Color.DarkGray,
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(32.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.5f), thickness = 8.dp)
                Spacer(modifier = Modifier.height(24.dp))

                // --- KHU VỰC NHẬN XÉT KHÁCH HÀNG ---
                Text(text = "Nhận xét khách hàng", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                currentReviews.forEach { review ->
                    ReviewItem(
                        name = review.name,
                        date = review.date,
                        content = review.content,
                        avatarUrl = review.avatarUrl
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = userReview,
                    onValueChange = { userReview = it },
                    placeholder = { Text("Chia sẻ trải nghiệm của bạn...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = CyanMain
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            if(userReview.isNotBlank()) {
                                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                                currentReviews.add(
                                    Review(
                                        name = "Bạn",
                                        date = currentDate,
                                        content = userReview,
                                        avatarUrl = "https://images.pexels.com/photos/771742/pexels-photo-771742.jpeg"
                                    )
                                )

                                Toast.makeText(context, "Đã gửi nhận xét thành công!", Toast.LENGTH_SHORT).show()
                                userReview = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanMain),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = "Gửi", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gửi đánh giá", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun FacilityItem(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(16.dp)).background(CyanLight), contentAlignment = Alignment.Center) { Icon(icon, contentDescription = label, tint = CyanMain, modifier = Modifier.size(28.dp)) }; Spacer(modifier = Modifier.height(8.dp)); Text(text = label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium) }
}

@Composable
fun ReviewItem(name: String, date: String, content: String, avatarUrl: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                Text(text = date, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = content, color = Color.DarkGray, fontSize = 14.sp, lineHeight = 22.sp)
    }
}