package com.hotelbooking.app.ui.screens.profile.itemprofilesetting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// XÓA DÒNG IMPORT .model.BookingModel VÌ NÓ NẰM CÙNG PACKAGE RỒI

@Composable
fun BookingHistoryItem(booking: BookingModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Booking #${booking.id}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "Check-in: ${booking.check_in_date}")
            Text(text = "Check-out: ${booking.check_out_date}")

            Spacer(modifier = Modifier.height(6.dp))

            // Lưu ý: Dùng Locale.US để định dạng số tiền nếu máy báo lỗi .format
            Text(
                text = "Total: %,.0f VND".format(booking.total_price),
                color = Color(0xFF1E88E5),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = booking.status.uppercase(),
                color = when (booking.status.lowercase()) { // Thêm lowercase để so sánh chính xác hơn
                    "completed" -> Color.Green
                    "pending" -> Color(0xFFFFA500)
                    "cancelled" -> Color.Red
                    else -> Color.Gray
                },
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}