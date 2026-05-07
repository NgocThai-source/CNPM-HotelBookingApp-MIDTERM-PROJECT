package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authState = viewModel.authState

    // Lắng nghe trạng thái từ ViewModel để báo Toast
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        } else if (authState is AuthState.Success) {
            Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF2196F3), Color(0xFF0D47A1))
    )

    Box(
        modifier = Modifier.fillMaxSize().background(gradientBackground),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Quên mật khẩu?", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Nhập email để nhận mã khôi phục", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email của bạn") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            // 1. Lưu email vào ViewModel
                            viewModel.email = email

                            // 2. Gọi hàm forgotPassword (không truyền tham số email)
                            viewModel.forgotPassword { isSuccess ->
                                if (isSuccess) {
                                    onBackToLogin()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    enabled = authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Gửi yêu cầu", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onBackToLogin) {
                    Text("Quay lại Đăng nhập", color = Color(0xFF1976D2))
                }
            }
        }
    }
}