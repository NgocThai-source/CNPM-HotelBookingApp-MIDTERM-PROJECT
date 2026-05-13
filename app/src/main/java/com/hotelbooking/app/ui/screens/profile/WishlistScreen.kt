package com.hotelbooking.app.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Data class
data class Hotel(
    val id: Int,
    val name: String,
    val location: String,
    val price: String,
    val rating: Double,
    val reviewCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(navController: NavController) {
    // Bảng màu yêu cầu
    val primaryColor = Color(0xFF00E5FF)
    val accentColor = Color(0xFFFFC107)
    val dangerColor = Color(0xFFEF5350)

    val favoriteHotels = remember {
        mutableStateListOf(
            Hotel(1, "InterContinental Danang", "Son Tra, Da Nang", "$250.00", 4.9, 1240),
            Hotel(2, "Pullman Beach Resort", "Ngu Hanh Son, Da Nang", "$145.00", 4.7, 850),
            Hotel(3, "Novotel Han River", "Hai Chau, Da Nang", "$110.00", 4.5, 920)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Wishlist", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFFDFDFD)
    ) { padding ->
        if (favoriteHotels.isEmpty()) {
            EmptyWishlist(primaryColor)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(favoriteHotels) { hotel ->
                    ModernHotelWishlistItem(hotel, primaryColor, accentColor, dangerColor) {
                        favoriteHotels.remove(hotel)
                    }
                }
            }
        }
    }
}

@Composable
fun ModernHotelWishlistItem(
    hotel: Hotel,
    primaryColor: Color,
    accentColor: Color,
    dangerColor: Color,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(primaryColor.copy(alpha = 0.2f), Color(0xFFE0E0E0))
                            )
                        )
                )

                // Remove Button (Dùng màu Đỏ EF5350FF)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                        .clickable { onRemove() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = null,
                        tint = dangerColor,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // Rating Badge (Dùng màu Vàng FFC107FF)
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, null, tint = accentColor, modifier = Modifier.size(14.dp))
                        Text(text = " ${hotel.rating}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Content Section
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = hotel.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = primaryColor, modifier = Modifier.size(14.dp))
                    Text(text = hotel.location, fontSize = 13.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = hotel.price,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF212121)
                        )
                        Text(
                            text = " / night",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Nút View Details (Dùng màu Cyan 00E5FFFF)
                    Text(
                        text = "View Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .background(primaryColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyWishlist(primaryColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = primaryColor.copy(alpha = 0.1f)
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.padding(30.dp),
                tint = primaryColor.copy(alpha = 0.4f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Your wishlist is empty", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        Text(
            "Tap the heart icon on any hotel to\nsave your favorite places for later.",
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}