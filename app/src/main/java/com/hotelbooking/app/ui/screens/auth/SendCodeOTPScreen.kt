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
import com.hotelbooking.app.ui.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun SendCodeOTPScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val authState = viewModel.authState

    var timeLeft by remember { mutableStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(true) }

    // Chỉ lắng nghe trạng thái Lỗi để hiện Toast
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isTimerRunning = false
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

                Text("Vui lòng nhập mã OTP gửi tới:", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                Text(viewModel.email, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = viewModel.otp,
                    onValueChange = { if (it.length <= 6) viewModel.otp = it },
                    modifier = Modifier.fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    textStyle = TextStyle(textAlign = TextAlign.Center, fontSize = 24.sp, letterSpacing = 8.sp, fontWeight = FontWeight.Bold),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("------", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (viewModel.otp.length == 6) {
                            viewModel.verifyOTP { isSuccess ->
                                if (isSuccess) {
                                    // BƯỚC QUAN TRỌNG NHẤT: Xóa trạng thái Success của OTP 
                                    // trước khi chuyển sang màn hình Reset Password
                                    viewModel.resetState()
                                    navController.navigate(Routes.RESET_PASSWORD)
                                }
                            }
                        } else {
                            Toast.makeText(context, "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show()
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
                        Text("Xác nhận OTP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        viewModel.forgotPassword { isSuccess ->
                            if (isSuccess) {
                                Toast.makeText(context, "Đã gửi lại mã OTP!", Toast.LENGTH_SHORT).show()
                                timeLeft = 60
                                isTimerRunning = true
                                viewModel.resetState()
                            }
                        }
                    },
                    enabled = !isTimerRunning && authState !is AuthState.Loading
                ) {
                    Text(
                        text = if (isTimerRunning) "Gửi lại mã (${timeLeft}s)" else "Gửi lại mã ngay",
                        color = if (isTimerRunning) Color.Gray else Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Quay lại", color = Color.Gray)
                }
            }
        }
    }
}
