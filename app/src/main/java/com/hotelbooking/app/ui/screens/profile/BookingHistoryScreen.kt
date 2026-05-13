package com.hotelbooking.app.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// --- 1. MODEL DỮ LIỆU (Giữ nguyên hoặc thêm field) ---
data class Booking(
    val id: String,
    val hotelName: String,
    val location: String, // Thêm địa điểm cho chuyên nghiệp
    val date: String,
    val price: String,
    val status: String,
    val statusColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(navController: NavController) {
    val bookings = listOf(
        Booking("#BK1024", "InterContinental Danang", "Sơn Trà, Đà Nẵng", "12/05 - 15/05/2026", "16.500.000đ", "Hoàn thành", Color(0xFF2E7D32)),
        Booking("#BK1055", "Pullman Beach Resort", "Ngũ Hành Sơn, Đà Nẵng", "20/06 - 22/06/2026", "6.400.000đ", "Sắp tới", Color(0xFF1565C0)),
        Booking("#BK0988", "Novotel Han River", "Hải Châu, Đà Nẵng", "01/04 - 02/04/2026", "2.800.000đ", "Đã hủy", Color(0xFFC62828))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar( // Dùng CenterAligned cho app quốc tế
                title = { Text("Lịch sử chuyến đi", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFBFBFB) // Màu nền trắng ngà nhẹ
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(bookings) { booking ->
                ModernBookingItem(booking)
            }
        }
    }
}

@Composable
fun ModernBookingItem(booking: Booking) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(24.dp)), // Border mỏng thay vì shadow đậm
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header: Status Badge & ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = booking.statusColor.copy(alpha = 0.1f),
                    shape = CircleShape // Badge bo tròn hoàn toàn nhìn hiện đại hơn
                ) {
                    Text(
                        text = booking.status,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = booking.statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(text = booking.id, color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tên khách sạn & Địa điểm
            Text(
                text = booking.hotelName,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = booking.location, fontSize = 13.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Thời gian lưu trú
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F9FA))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.CalendarMonth, null, tint = Color(0xFF424242), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = booking.date, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF424242))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer: Tổng tiền & Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tổng thanh toán", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = booking.price,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1976D2),
                        fontSize = 18.sp
                    )
                }

                Button(
                    onClick = { /* Logic */ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black), // Nút đen nhìn rất "Lux"
                    contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text("Chi tiết", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}