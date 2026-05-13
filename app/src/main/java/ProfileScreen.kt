
package com.hotelbooking.app.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(navController: NavController) {
    val name = "Nguyễn Văn A"
    val email = "vannguyen11a21@gmail.com"

    // Sử dụng Column fillMaxSize mà không có verticalScroll trực tiếp ở đây
    // để có thể dùng weight(1f) đẩy nút xuống dưới.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
    ) {
        // --- PHẦN NỘI DUNG CÓ THỂ CUỘN ---
        Column(
            modifier = Modifier
                .weight(1f) // Chiếm toàn bộ không gian trống phía trên
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER ---
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1976D2).copy(alpha = 0.1f), Color.Transparent)
                            )
                        )
                )

                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(110.dp).shadow(8.dp, CircleShape),
                        shape = CircleShape,
                        color = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            tint = Color(0xFFE0E0E0)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = email, fontSize = 14.sp, color = Color.Gray)
                }
            }

            // --- CÁC MỤC MENU ---
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                SectionTitle("TÀI KHOẢN CỦA TÔI")
                ModernCard {
                    MenuRow("Thông tin cá nhân", Icons.Outlined.Person) {
                        navController.navigate("edit_profile")
                    }
                    CustomDivider()
                    MenuRow("Danh sách yêu thích", Icons.Outlined.FavoriteBorder) {
                        navController.navigate("wishlist")
                    }
                    CustomDivider()
                    MenuRow("Lịch sử đặt phòng", Icons.Outlined.ListAlt) {
                        navController.navigate("booking_history")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("CÀI ĐẶT & BẢO MẬT")
                ModernCard {
                    MenuRow("Đổi Mật Khẩu", Icons.Outlined.Lock) {
                        navController.navigate("privacy_security")
                    }
                }
            }
        }

        // --- NÚT ĐĂNG XUẤT (LUÔN Ở DƯỚI CÙNG) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Button(
                onClick = { /* Logout Logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F1))
            ) {
                Text(
                    text = "Đăng xuất",
                    color = Color(0xFFFF5252),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// --- THÀNH PHẦN UI TÙY CHỈNH (Giữ nguyên logic thiết kế) ---

@Composable
fun ModernCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        content = content
    )
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
        fontSize = 12.sp,
        color = Color(0xFF9E9E9E),
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

@Composable
fun MenuRow(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF424242),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF212121)
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFBDBDBD),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun CustomDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(0.8.dp)
            .background(Color(0xFFF5F5F5))
    )
}

