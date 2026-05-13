package com.hotelbooking.app.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Data class (Giữ nguyên hoặc thêm field)
data class Hotel(
    val id: Int,
    val name: String,
    val location: String,
    val price: String,
    val rating: Double,
    val reviewCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(navController: NavController) {
    val favoriteHotels = remember {
        mutableStateListOf(
            Hotel(1, "InterContinental Danang", "Sơn Trà, Đà Nẵng", "5.500.000đ", 4.9, 1240),
            Hotel(2, "Pullman Beach Resort", "Ngũ Hành Sơn, Đà Nẵng", "3.200.000đ", 4.7, 850),
            Hotel(3, "Novotel Han River", "Hải Châu, Đà Nẵng", "2.800.000đ", 4.5, 920)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Yêu thích của tôi", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFBFBFB)
    ) { padding ->
        if (favoriteHotels.isEmpty()) {
            EmptyWishlist()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(favoriteHotels) { hotel ->
                    ModernHotelWishlistItem(hotel) {
                        favoriteHotels.remove(hotel)
                    }
                }
            }
        }
    }
}

@Composable
fun ModernHotelWishlistItem(hotel: Hotel, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Detail navigation */ },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Phần hình ảnh lớn
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Giả lập hình ảnh với Gradient và Placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))
                            )
                        )
                )

                // Nút xóa (Trái tim đỏ) ở góc ảnh
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                        .clickable { onRemove() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // Badge đánh giá nổi trên ảnh
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                        Text(text = " ${hotel.rating}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Phần nội dung bên dưới
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = hotel.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Text(text = hotel.location, fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dòng giá tiền
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = hotel.price,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1976D2)
                        )
                        Text(
                            text = " / đêm",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    Text(
                        text = "Xem chi tiết",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyWishlist() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = Color(0xFFF5F5F5)
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.padding(30.dp),
                tint = Color.LightGray
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Chưa có danh sách yêu thích", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        Text(
            "Hãy nhấn vào biểu tượng trái tim để\nlưu lại những nơi bạn muốn đến.",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}