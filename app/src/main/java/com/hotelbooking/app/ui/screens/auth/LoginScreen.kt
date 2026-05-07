package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hotelbooking.app.data.model.LoginRequest

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit, // Đã gộp tham số từ nhánh forgotpass
    viewModel: AuthViewModel = viewModel()  // Đã gộp ViewModel từ nhánh HEAD
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val authState = viewModel.authState

    // Lắng nghe trạng thái lỗi để báo Toast
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        } else if (authState is AuthState.Success) {
            Toast.makeText(context, authState.message, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
    }

    // Tạo nền Gradient
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
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo / Icon
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Logo",
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF1976D2)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Chào mừng trở lại!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                Text("Đăng nhập để đặt phòng ngay", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(32.dp))

                // Ô nhập Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email của bạn") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Ô nhập Mật khẩu
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Password") },
                    trailingIcon = {
                        TextButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "ẨN" else "HIỆN", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Nút Quên mật khẩu
                TextButton(
                    onClick = onNavigateToForgotPassword, // Chuyển hướng chuẩn xác
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Quên mật khẩu?", color = Color(0xFF1976D2), fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // NÚT ĐĂNG NHẬP
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            // Gọi API qua ViewModel
                            val request = LoginRequest(email, password)
                            viewModel.login(request) { isSuccess ->
                                if (isSuccess) {
                                    onLoginClick()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
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
                        Text("Đăng nhập", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // NÚT ĐĂNG KÝ
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Chưa có tài khoản? ", color = Color.Gray)
                    TextButton(onClick = onNavigateToRegister) {
                        Text("Đăng ký ngay", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}