package com.hotelbooking.app.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen
import com.hotelbooking.app.ui.screens.profile.ProfileScreen
import com.hotelbooking.app.ui.screens.profile.Routes
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.BookingHistoryItem
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.PaymentItem
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.ProfileSettingItem
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.EditProfileScreen
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.BookingModel
import com.hotelbooking.app.ui.screens.profile.ProfileUiState
import com.hotelbooking.app.ui.screens.profile.ProfileViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val animationDuration = 850
    val easingCurve = FastOutSlowInEasing

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeIn(tween(animationDuration))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeOut(tween(animationDuration))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeIn(tween(animationDuration))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeOut(tween(animationDuration))
        }
    ) {
        // 1. Màn hình Đăng nhập
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // 2. Màn hình Home
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        // 3. Màn hình Menu Profile chính
        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }

        // 4. Màn hình Thông tin cá nhân (Của bạn)
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen()
        }

        // 5. Màn hình Lịch sử đặt phòng (Của team)
        // 5. Màn hình Lịch sử đặt phòng (Của team)
        composable(Routes.BOOKING_HISTORY) {
            BookingHistoryItem(
                booking = BookingModel(
                    id = 0,
                    check_in_date = "",
                    check_out_date = "",
                    status = "",
                    total_price = 0.0
                )
            )
        }

        // 6. Màn hình Phương thức thanh toán (Của team)
        composable(Routes.PAYMENT_METHOD) {
            PaymentItem(navController = navController)
        }
    }
}

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    // 1. Tự động gọi API khi vào màn hình
    LaunchedEffect(Unit) {
        // Thay ID này bằng ID thật bạn copy từ bảng profile_users trên Supabase
        viewModel.fetchUserProfile("8f187c60-14f3-48f8-a9c9-8f6177d...")
    }

    val state = viewModel.uiState

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            is ProfileUiState.Loading -> CircularProgressIndicator() // Đang tải dữ liệu
            is ProfileUiState.Success -> {
                // 2. KẾT NỐI API THÀNH CÔNG: Đổ dữ liệu từ Backend vào giao diện
                ProfileSettingItem(
                    fullName = state.user.full_name, // Dữ liệu từ API
                    email = state.user.email,         // Dữ liệu từ API
                    phone = state.user.phone,         // Dữ liệu từ API
                    createdDate = state.user.created_at.substring(0, 10), // Cắt lấy YYYY-MM-DD
                    password = "********" // Mật khẩu thường không trả về qua API này để bảo mật
                )
            }
            is ProfileUiState.Error -> {
                Text("Lỗi kết nối: ${state.message}", color = Color.Red)
            }
        }
    }
}