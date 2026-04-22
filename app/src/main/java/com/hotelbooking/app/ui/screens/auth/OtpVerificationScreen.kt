package com.hotelbooking.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay //Import thư viện thời gian

@Composable
fun OtpVerificationScreen(
    email: String, // Nhận email thực tế từ ngoài truyền vào
    onVerifySuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }

    // BIẾN THỜI GIAN ĐẾM NGƯỢC
    var timeLeft by remember { mutableStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(true) }

    // LOGIC ĐẾM NGƯỢC TỰ ĐỘNG
    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            while (timeLeft > 0) {
                delay(1000L) // Chờ 1 giây
                timeLeft--   // Trừ đi 1
            }
            isTimerRunning = false // Về 0 thì dừng đồng hồ
        }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF2196F3), Color(0xFF0D47A1))
    )

    Box(modifier = Modifier.fillMaxSize().background(gradientBackground), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(32.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "OTP Verification", modifier = Modifier.size(64.dp), tint = Color(0xFF1976D2))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Xác thực Email", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(8.dp))

                Text("Vui lòng nhập mã OTP gồm 6 chữ số vừa được gửi đến email:", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                // HIỂN THỊ EMAIL THẬT
                Text(email, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = otpCode,
                    onValueChange = { if (it.length <= 6) otpCode = it },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 24.sp, letterSpacing = 8.sp, fontWeight = FontWeight.Bold),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("------", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, letterSpacing = 8.sp) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onVerifySuccess,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    enabled = otpCode.length == 6
                ) {
                    Text("Xác nhận mã", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Chưa nhận được mã?", color = Color.Gray, fontSize = 14.sp)

                // NÚT GỬI LẠI MÃ ĐỘNG
                TextButton(
                    onClick = {
                        // Reset lại đồng hồ về 30s và chạy lại
                        timeLeft = 30
                        isTimerRunning = true
                        /* TODO: Xử lý gọi lại API gửi mã OTP */
                    },
                    enabled = !isTimerRunning // Đồng hồ đang chạy thì KHÔNG cho bấm
                ) {
                    Text(
                        text = if (isTimerRunning) "Gửi lại mã (${timeLeft}s)" else "Gửi lại mã ngay",
                        color = if (isTimerRunning) Color.Gray else Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onBackClick) { Text("Quay lại", color = Color.Gray) }
            }
        }
    }
}