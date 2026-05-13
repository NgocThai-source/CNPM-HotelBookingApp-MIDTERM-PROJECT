package com.hotelbooking.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Định nghĩa bảng màu chuyên nghiệp cho App đặt phòng
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),      // Màu xanh chủ đạo
    secondary = Color(0xFF03DAC5),
    background = Color(0xFFF7F7F7),   // Màu nền xám nhẹ cho toàn trang
    surface = Color.White,            // Màu nền cho các Card, Box trắng
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun HotelBookingAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Hỗ trợ chế độ tối nếu cần
    content: @Composable () -> Unit
) {
    // Hiện tại chúng ta dùng LightColorScheme cho cả 2 để đồng bộ giao diện Profile
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}