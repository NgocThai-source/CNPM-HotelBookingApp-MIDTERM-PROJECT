package com.hotelbooking.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.auth.RegisterScreen
import com.hotelbooking.app.ui.screens.auth.AuthViewModel
import com.hotelbooking.app.ui.screens.home.HomeScreen


@Composable
fun NavGraph() {
    val navController = rememberNavController()
    // Khởi tạo ViewModel nếu các màn hình Auth vẫn cần
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                // Có thể bỏ dòng viewModel = authViewModel nếu file Login của bạn không cần
                viewModel = authViewModel,
                onLoginClick = {
                    // Chuyển sang Home và xóa Login khỏi ngăn xếp
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    // Mở trang Đăng ký
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                // Có thể bỏ dòng viewModel = authViewModel nếu file Register của bạn không cần
                viewModel = authViewModel,
                onRegisterSuccess = {
                    // Đăng ký xong quay về Login
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onBackToLogin = {
                    // Ấn nút quay lại Login
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen()
        }
    }
}