package com.hotelbooking.app.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current

    // State quản lý dữ liệu nhập
    var name by remember { mutableStateOf("Nguyễn Văn A") }
    var phone by remember { mutableStateOf("0901234567") }
    val email = "vannguyen11a21@gmail.com" // Không dùng State vì không cho đổi

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thông tin cá nhân", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. AVATAR (GIỮ NGUYÊN) ---
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(modifier = Modifier.size(100.dp), shape = CircleShape, color = Color(0xFFF5F5F5)) {
                    Icon(Icons.Outlined.Person, null, modifier = Modifier.padding(20.dp), tint = Color.LightGray)
                }
                Surface(modifier = Modifier.size(32.dp).clip(CircleShape), color = Color.Black, contentColor = Color.White) {
                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.padding(8.dp).size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. CÁC TRƯỜNG NHẬP LIỆU ---

            // HỌ TÊN (Được sửa)
            ModernInputField(
                value = name,
                onValueChange = { name = it },
                label = "Họ và tên",
                icon = Icons.Outlined.Person,
                enabled = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // SỐ ĐIỆN THOẠI (Được sửa + Check 10 số)
            ModernInputField(
                value = phone,
                onValueChange = { if (it.length <= 10) phone = it }, // Giới hạn tối đa 10 số khi nhập
                label = "Số điện thoại",
                icon = Icons.Outlined.PhoneIphone,
                enabled = true,
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(20.dp))

            // EMAIL (KHÔNG ĐƯỢC SỬA)
            ModernInputField(
                value = email,
                onValueChange = {},
                label = "Email (Không thể thay đổi)",
                icon = Icons.Outlined.Email,
                enabled = false // Vô hiệu hóa chỉnh sửa
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(40.dp))

            // --- 3. NÚT LƯU VỚI LOGIC CHECK ---
            Button(
                onClick = {
                    if (phone.length != 10) {
                        Toast.makeText(context, "Số điện thoại phải đủ 10 chữ số", Toast.LENGTH_SHORT).show()
                    } else if (name.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show()
                    } else {
                        // Logic lưu thành công
                        Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Lưu thay đổi", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    enabled: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) Color.Gray else Color.LightGray,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (enabled) Color.Black else Color.LightGray
                )
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color(0xFFEEEEEE),
                disabledBorderColor = Color(0xFFF5F5F5),
                containerColor = if (enabled) Color(0xFFFBFBFB) else Color(0xFFF5F5F5),
                disabledTextColor = Color.Gray
            )
        )
    }
}