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
    val name = "Alex Nguyen"
    val email = "vannguyen11a21@gmail.com"

    // Bảng màu yêu cầu
    val primaryColor = Color(0xFF00E5FF)
    val accentColor = Color(0xFFFFC107)
    val dangerColor = Color(0xFFEF5350)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDFDFD))
    ) {
        // --- SCROLLABLE CONTENT ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER ---
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(primaryColor.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                )

                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier
                            .size(110.dp)
                            .shadow(12.dp, CircleShape),
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(2.dp, primaryColor) // Viền màu chủ đạo quanh Avatar
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().padding(4.dp),
                            tint = primaryColor.copy(alpha = 0.3f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF212121))
                    Text(text = email, fontSize = 14.sp, color = Color.Gray)
                }
            }

            // --- MENU SECTIONS ---
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                SectionTitle("MY ACCOUNT", primaryColor)
                ModernCard {
                    MenuRow("Personal Information", Icons.Outlined.Person, primaryColor) {
                        navController.navigate("edit_profile")
                    }
                    CustomDivider()
                    MenuRow("My Wishlist", Icons.Outlined.FavoriteBorder, primaryColor) {
                        navController.navigate("wishlist")
                    }
                    CustomDivider()
                    MenuRow("Booking History", Icons.Outlined.ListAlt, primaryColor) {
                        navController.navigate("booking_history")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle("SETTINGS & SECURITY", primaryColor)
                ModernCard {
                    // Dùng màu vàng (Accent) cho bảo mật để tạo điểm nhấn
                    MenuRow("Change Password", Icons.Outlined.Lock, accentColor) {
                        navController.navigate("privacy_security")
                    }
                }
            }
        }

        // --- LOGOUT BUTTON ---
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = dangerColor.copy(alpha = 0.1f) // Nền đỏ nhạt
                ),
                border = BorderStroke(1.dp, dangerColor.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = dangerColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    color = dangerColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ModernCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Tăng nhẹ shadow cho nổi trên nền trắng
        content = content
    )
}

@Composable
fun SectionTitle(title: String, color: Color) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 4.dp, bottom = 12.dp),
        fontSize = 12.sp,
        color = color, // Dùng màu chủ đạo cho tiêu đề mục
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )
}

@Composable
fun MenuRow(title: String, icon: ImageVector, iconColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon được đổi màu theo bộ nhận diện
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
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