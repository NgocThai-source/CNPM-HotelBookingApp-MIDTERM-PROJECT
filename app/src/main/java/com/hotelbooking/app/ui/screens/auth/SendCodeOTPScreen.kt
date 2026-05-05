package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SendCodeOTPScreen( // Đổi tên cho khớp với NavGraph
    navController: NavController,
    viewModel: AuthViewModel // Nhận túi dữ liệu chung
) {
    val context = LocalContext.current

    // BIẾN THỜI GIAN ĐẾM NGƯỢC (Giữ nguyên logic cực xịn của Kiệt)
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

                // HIỂN THỊ EMAIL THẬT TỪ VIEWMODEL
                Text(viewModel.email, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = viewModel.otp, // Lưu thẳng mã OTP vào ViewModel
                    onValueChange = { if (it.length <= 6) viewModel.otp = it },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 24.sp, letterSpacing = 8.sp, fontWeight = FontWeight.Bold),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("------", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, letterSpacing = 8.sp) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        // Chuyển sang màn hình tạo mật khẩu mới khi bấm xác nhận
                        navController.navigate("create_new_password")
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    enabled = viewModel.otp.length == 6 // Phải nhập đủ 6 số mới cho bấm
                ) {
                    Text("Xác nhận mã", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Chưa nhận được mã?", color = Color.Gray, fontSize = 14.sp)

                // NÚT GỬI LẠI MÃ ĐỘNG ĐÃ TÍCH HỢP GỌI BACKEND
                TextButton(
                    onClick = {
                        // Gọi lại hàm quên mật khẩu để Backend gửi OTP mới
                        viewModel.forgotPassword { isSuccess ->
                            if (isSuccess) {
                                Toast.makeText(context, "Đã gửi lại mã OTP!", Toast.LENGTH_SHORT).show()
                                // Reset lại đồng hồ về 30s và chạy lại
                                timeLeft = 30
                                isTimerRunning = true
                                viewModel.resetState()
                            } else {
                                Toast.makeText(context, "Gửi thất bại, thử lại sau", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isTimerRunning // Đồng hồ đang chạy thì KHÔNG cho bấm
                ) {
                    // Nếu đang gọi API thì hiện loading nhỏ, không thì hiện Text
                    if (viewModel.authState == AuthState.Loading && !isTimerRunning) {
                        CircularProgressIndicator(color = Color(0xFF1976D2), modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (isTimerRunning) "Gửi lại mã (${timeLeft}s)" else "Gửi lại mã ngay",
                            color = if (isTimerRunning) Color.Gray else Color(0xFF1976D2),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Quay lại", color = Color.Gray)
                }
            }
        }
    }
}