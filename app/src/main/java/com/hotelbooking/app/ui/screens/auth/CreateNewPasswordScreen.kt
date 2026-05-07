package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hotelbooking.app.ui.navigation.Routes

@Composable
fun CreateNewPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel // Nhận ViewModel dùng chung
) {
    val context = LocalContext.current

    // Biến lưu mật khẩu xác nhận (confirm). Biến mật khẩu chính (newPassword) sẽ nằm trong ViewModel
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Lắng nghe kết quả từ Backend để xử lý thành công / thất bại
    LaunchedEffect(viewModel.authState) {
        when (val state = viewModel.authState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show()
                viewModel.resetState()
                // Xóa toàn bộ stack và đẩy thẳng về trang Đăng nhập
                navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
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
                Icon(imageVector = Icons.Filled.Lock, contentDescription = "Reset Password", modifier = Modifier.size(64.dp), tint = Color(0xFF1976D2))
                Spacer(modifier = Modifier.height(16.dp))

                Text("Đặt lại mật khẩu", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Vui lòng tạo mật khẩu mới an toàn để bảo vệ tài khoản của bạn.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = viewModel.newPassword, // Lưu thẳng mật khẩu mới vào ViewModel
                    onValueChange = { viewModel.newPassword = it },
                    label = { Text("Mật khẩu mới") },
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

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword, // Biến này giữ nguyên vì Backend không cần
                    onValueChange = { confirmPassword = it },
                    label = { Text("Xác nhận mật khẩu") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Confirm Password") },
                    trailingIcon = {
                        TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Text(if (confirmPasswordVisible) "ẨN" else "HIỆN", color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = viewModel.newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && viewModel.newPassword != confirmPassword
                )

                // Cảnh báo nếu 2 mật khẩu không khớp
                if (viewModel.newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && viewModel.newPassword != confirmPassword) {
                    Text("Mật khẩu xác nhận không khớp!", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start).padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        // Gọi hàm chốt hạ để bắn thẳng lên Backend
                        viewModel.verifyAndResetPassword { _ -> }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                    // Nút chỉ sáng lên khi 2 pass giống nhau VÀ không đang trong quá trình tải
                    enabled = viewModel.newPassword.isNotEmpty() && viewModel.newPassword == confirmPassword && viewModel.authState != AuthState.Loading
                ) {
                    if (viewModel.authState == AuthState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Đổi mật khẩu", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}