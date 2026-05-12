package com.hotelbooking.app.ui.screens.profile.itemprofilesetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

// Giữ nguyên các import màu sắc của bạn
// import com.hotelbooking.app.ui.screens.home.CyanLight
// import com.hotelbooking.app.ui.screens.home.CyanMain

// Fallback color để code chạy được trên Preview nếu thiếu file màu của bạn
val CyanMain = Color(0xFF00BCD4)

@Composable
fun ProfileSettingItem(
    fullName: String,
    email: String,
    phone: String,
    createdDate: String,
    password: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER: Avatar & Name ---
            Surface(
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                color = CyanMain.copy(alpha = 0.15f) // Viền ngoài mờ
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = CyanMain
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = fullName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E1E)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- DETAILS SECTION ---
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoRow(
                    icon = Icons.Default.Email,
                    label = "Địa chỉ Email",
                    text = email
                )

                DividerItem()

                InfoRow(
                    icon = Icons.Default.Phone,
                    label = "Số điện thoại",
                    text = phone
                )

                DividerItem()

                InfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Ngày tham gia",
                    text = createdDate
                )

                DividerItem()

                PasswordRow(password = password)
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(CyanMain.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = CyanMain
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
fun PasswordRow(password: String) {
    var showPassword by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(CyanMain.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Password",
                modifier = Modifier.size(22.dp),
                tint = CyanMain
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info Text
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Mật khẩu",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (showPassword) password else "•".repeat(password.length),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                letterSpacing = if (showPassword) TextUnit.Unspecified else 2.sp
            )
        }

        // Toggle Button
        IconButton(onClick = { showPassword = !showPassword }) {
            Icon(
                imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun DividerItem() {
    Divider( // Sửa DividerItem thành Divider ở dòng này
        modifier = Modifier.padding(start = 60.dp, top = 4.dp, bottom = 4.dp),
        thickness = 1.dp,
        color = Color(0xFFF0F0F0)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun ProfileSettingItemPreview() {}