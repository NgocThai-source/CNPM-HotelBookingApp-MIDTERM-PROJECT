package com.hotelbooking.app.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hotelbooking.app.ui.screens.auth.*
import com.hotelbooking.app.ui.screens.detail.HotelDetailScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen

// THÊM IMPORT VIEWMODEL CHO TRANG CHI TIẾT VÀO ĐÂY (Bạn có thể Alt + Enter nếu nó báo đỏ nhé)
import com.hotelbooking.app.ui.screens.detail.HotelDetailViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    val animationDuration = 850
    val easingCurve = FastOutSlowInEasing

    // Dark mode state lives at NavGraph level, survives config changes via rememberSaveable
    var isDarkMode by rememberSaveable { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { slideInHorizontally(initialOffsetX = { it / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeIn(tween(animationDuration)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeOut(tween(animationDuration)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeIn(tween(animationDuration)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeOut(tween(animationDuration)) }
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                isDarkMode = isDarkMode,
                onLoginClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                isDarkMode = isDarkMode,
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                viewModel = authViewModel,
                isDarkMode = isDarkMode,
                onBackToLogin = { navController.popBackStack() },
                onNavigateToOTP = {
                    navController.navigate(Routes.VERIFY_OTP)
                }
            )
        }

        composable(Routes.VERIFY_OTP) {
            SendCodeOTPScreen(
                navController = navController,
                viewModel = authViewModel,
                isDarkMode = isDarkMode
            )
        }

        composable(Routes.RESET_PASSWORD) {
            CreateNewPasswordScreen(
                navController = navController,
                viewModel = authViewModel,
                isDarkMode = isDarkMode
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                isDarkMode = isDarkMode,
                onThemeToggle = { isDarkMode = !isDarkMode }
            )
        }

        // --- ĐÂY LÀ ĐOẠN ĐÃ ĐƯỢC SỬA LẠI ĐỂ KHỚP VỚI HOMESCREEN VÀ DETAILSCREEN ---
        composable(
            route = "hotel_detail/{hotelId}", // Đổi từ hotelName sang hotelId
            arguments = listOf(navArgument("hotelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""

            // Khởi tạo ViewModel cho màn hình chi tiết
            val hotelDetailViewModel: HotelDetailViewModel = viewModel()

            HotelDetailScreen(
                navController = navController,
                hotelId = hotelId, // Truyền hotelId
                viewModel = hotelDetailViewModel, // Truyền ViewModel
                isDarkMode = isDarkMode
            )
        }

    }
}