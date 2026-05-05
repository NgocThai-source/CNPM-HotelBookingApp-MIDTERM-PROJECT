package com.hotelbooking.app.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel // Import thư viện tạo ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hotelbooking.app.ui.screens.auth.AuthViewModel // Import AuthViewModel của bạn
import com.hotelbooking.app.ui.screens.auth.ForgotPasswordScreen
import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    // Khởi tạo AuthViewModel dùng chung cho toàn bộ chuỗi Quên mật khẩu
    val authViewModel: AuthViewModel = viewModel()

    val animationDuration = 850
    val easingCurve = FastOutSlowInEasing

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeIn(animationSpec = tween(animationDuration))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeOut(animationSpec = tween(animationDuration))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeIn(animationSpec = tween(animationDuration))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth / 4 },
                animationSpec = tween(animationDuration, easing = easingCurve)
            ) + fadeOut(animationSpec = tween(animationDuration))
        }
    ) {

        // 1. Nhánh Đăng nhập
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } }
                },
                onNavigateToRegister = {
                    // Nút Đăng ký ở nhánh này tạm thời để trống, không thực hiện chuyển trang
                    // Chờ đến khi gộp với nhánh Register sau.
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        // 2. Nhánh Quên mật khẩu (Bước 1: Nhập Email)
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                viewModel = authViewModel, // Truyền ViewModel chung vào đây
                onSendClick = {
                    // Đổi từ popBackStack thành điều hướng sang trang OTP
                    navController.navigate("send_code_otp")
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 2.1 Nhánh Nhập OTP (Bước 2: Kế thừa từ ForgotPassword)
        composable("send_code_otp") {
            // TODO: Bỏ comment khi bạn gửi file SendCodeOTPScreen
            /* SendCodeOTPScreen(
                viewModel = authViewModel, // Tiếp tục truyền ViewModel này vào
                onVerifyClick = {
                    navController.navigate("create_new_password")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
            */
        }

        // 2.2 Nhánh Tạo mật khẩu mới (Bước 3: Gửi 3 thứ lên Backend)
        composable("create_new_password") {
            // TODO: Bỏ comment khi bạn gửi file CreateNewPasswordScreen
            /*
            CreateNewPasswordScreen(
                viewModel = authViewModel, // Nhận đủ bộ Email, OTP từ 2 màn trước
                onConfirmClick = {
                    // Thành công thì quay thẳng về màn hình Đăng nhập
                    navController.navigate(Routes.LOGIN) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
            */
        }

        // 3. Nhánh Trang chủ
        composable(Routes.HOME) {
            HomeScreen(
                onLogoutClick = {
                    navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }
    }
}