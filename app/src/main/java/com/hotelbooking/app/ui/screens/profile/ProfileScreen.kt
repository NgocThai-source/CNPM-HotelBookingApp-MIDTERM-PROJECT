package com.hotelbooking.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.hotelbooking.app.ui.screens.home.CyanLight
import com.hotelbooking.app.ui.screens.home.CyanMain
import com.hotelbooking.app.ui.screens.home.HomeBottomNav
import com.hotelbooking.app.ui.screens.home.HomeScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        bottomBar = { HomeBottomNav(navController) },
        containerColor = Color(0xFFF8F9FA) // Cùng màu nền với HomeScreen
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(modifier = Modifier.height(32.dp)) }

            // --- Header: Ảnh đại diện và Tên ---
            item {
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = "https://images.pexels.com/photos/712513/pexels-photo-712513.jpeg",
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CyanMain)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Trần Văn Demo", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("demo.user@email.com", fontSize = 14.sp, color = Color.Gray)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            //Tài khoản
            item { ProfileSectionTitle("Tài khoản của tôi") }
            item {
                ProfileCard {
                    ProfileMenuItem(Icons.Filled.Person, "Thông tin cá nhân", onClink = { navController.navigate(Routes.EDIT_PROFILE) })
                    ProfileMenuItem(Icons.Filled.CreditCard, "Phương thức thanh toán", onClink = { navController.navigate(Routes.PAYMENT_METHOD) })
                    ProfileMenuItem(Icons.Filled.Favorite, "Danh sách yêu thích", onClink = { navController.navigate("edit_profile") })
                    ProfileMenuItem(Icons.Filled.History, "Lịch sử đặt phòng", isLast = true, onClink = { navController.navigate(Routes.BOOKING_HISTORY)})
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            //Cài đặt & Hỗ trợ
            item { ProfileSectionTitle("Cài đặt & Hỗ trợ") }
            item {
                ProfileCard {
                    ProfileMenuItem(Icons.Filled.Notifications, "Thông báo", onClink = { navController.navigate("edit_profile") })
                    ProfileMenuItem(Icons.Filled.Security, "Bảo mật & Mật khẩu", onClink = { navController.navigate("edit_profile") })
                    ProfileMenuItem(Icons.Filled.HelpCenter, "Trung tâm trợ giúp", onClink = { navController.navigate("edit_profile") })
                    ProfileMenuItem(Icons.Filled.Language, "Ngôn ngữ", trailingText = "Tiếng Việt", isLast = true, onClink = { navController.navigate("edit_profile") })
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            //Nút Đăng xuất
            item {
                Button(
                    onClick = {
                        navController.navigate("login"){
                            popUpTo(navController.graph.startDestinationId){
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        color = Color.Black
    )
}

@Composable
fun ProfileCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp), content = content)
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    trailingText: String? = null,
    isLast: Boolean = false,
    onClink: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClink() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CyanLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = CyanMain, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))

        if (trailingText != null) {
            Text(text = trailingText, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
        }

        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }

    if (!isLast) {
        Divider(
            modifier = Modifier.padding(start = 68.dp, end = 16.dp),
            thickness = 0.5.dp,
            color = Color(0xFFF1F1F1)
        )
    }
}
