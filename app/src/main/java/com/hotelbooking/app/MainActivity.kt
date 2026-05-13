
package com.hotelbooking.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hotelbooking.app.ui.screens.profile.ProfileScreen
import com.hotelbooking.app.ui.screens.profile.EditProfileScreen
import com.hotelbooking.app.ui.screens.profile.BookingHistoryScreen
import com.hotelbooking.app.ui.screens.profile.PrivacySecurityScreen
import com.hotelbooking.app.ui.screens.profile.WishlistScreen
import com.hotelbooking.app.ui.theme.HotelBookingAppTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("ComposableDestinationInComposeScope")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HotelBookingAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "profile"
                    ) {
                        // 1. Màn hình Profile chính
                        composable("profile") {
                            ProfileScreen(navController = navController)
                        }

                        // 2. Màn hình Chỉnh sửa thông tin
                        composable("edit_profile") {
                            EditProfileScreen(navController = navController)
                        }

                        // 3. Màn hình Lịch sử đặt phòng
                        composable("booking_history") {
                            BookingHistoryScreen(navController = navController)
                        }

                        // 4. Màn hình Danh sách yêu thích
                        composable("wishlist") {
                            WishlistScreen(navController = navController)
                        } // <--- Bạn từng thiếu/sai dấu đóng ngoặc ở đây

                        // 5. Màn hình Quyền riêng tư và bảo mật
                        composable("privacy_security") {
                            PrivacySecurityScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

