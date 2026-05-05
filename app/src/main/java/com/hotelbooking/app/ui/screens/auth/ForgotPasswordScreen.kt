package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel, // Dùng ViewModel để hứng dữ liệu và gọi Backend
    onSendClick: () -> Unit,  // Không cần truyền String nữa vì ViewModel đã giữ biến email
    onBackToLogin: () -> Unit
) {
    val context = LocalContext.current
    val authState = viewModel.authState

    // Lắng nghe trạng thái lỗi từ Backend để hiển thị Toast
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
            viewModel.resetState() // Reset lại trạng thái sau khi thông báo xong
        }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF2196F3), Color(0xFF0D47A1))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon làm điểm nhấn
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Forgot Password",
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF1976D2)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Khôi phục mật khẩu",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Vui lòng nhập địa chỉ email đã đăng ký. Chúng tôi sẽ gửi cho bạn một đường dẫn để đặt lại mật khẩu.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = viewModel.email, // Lấy email từ túi chung
                    onValueChange = { viewModel.email = it }, // Lưu trực tiếp vào túi chung
                    label = { Text("Email của bạn") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (viewModel.email.isBlank()) {
                            Toast.makeText(context, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                        } else {
                            // Gọi hàm gửi API
                            viewModel.forgotPassword { isSuccess ->
                                if (isSuccess) {
                                    Toast.makeText(context, "Đã gửi mã xác nhận!", Toast.LENGTH_SHORT).show()
                                    viewModel.resetState()
                                    onSendClick() // Chuyển sang màn hình SendCodeOTP
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    enabled = authState != AuthState.Loading // Khóa nút khi đang tải (đang gọi API)
                ) {
                    // Hiển thị vòng xoay nếu đang Loading, ngược lại hiện chữ
                    if (authState == AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Gửi mã xác nhận", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onBackToLogin) {
                    Text("Quay lại Đăng nhập", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}