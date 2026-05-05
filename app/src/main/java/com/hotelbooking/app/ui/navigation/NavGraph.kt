package com.hotelbooking.app.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hotelbooking.app.ui.screens.auth.AuthViewModel
import com.hotelbooking.app.ui.screens.auth.ForgotPasswordScreen
import com.hotelbooking.app.ui.screens.auth.SendCodeOTPScreen
import com.hotelbooking.app.ui.screens.auth.CreateNewPasswordScreen
import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen

@Composable
fun NavGraph() { // ĐÃ BỎ isDarkMode và onThemeToggle
    val navController = rememberNavController()

    // KHỞI TẠO VIEWMODEL CHUNG: Đây là "cái túi" giữ Email, OTP và Password
    val authViewModel: AuthViewModel = viewModel()

    val animationDuration = 850
    val easingCurve = FastOutSlowInEasing

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeIn(tween(animationDuration))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeOut(tween(animationDuration))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeIn(tween(animationDuration))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeOut(tween(animationDuration))
        }
    ) {
        // 1. Màn hình Đăng nhập
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onNavigateToRegister = {
                    // Nút đăng ký
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        // 2. Bước 1: Quên mật khẩu (Nhập Email)
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                onSendClick = {
                    navController.navigate("send_code_otp")
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Bước 2: Nhập OTP
        composable("send_code_otp") {
            SendCodeOTPScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }

        // 4. Bước 3: Tạo mật khẩu mới
        composable("create_new_password") {
            CreateNewPasswordScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }

        // 5. Trang chủ (Đã đưa về nguyên bản, bỏ Dark Mode)
        composable(Routes.HOME) {
            HomeScreen(
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}