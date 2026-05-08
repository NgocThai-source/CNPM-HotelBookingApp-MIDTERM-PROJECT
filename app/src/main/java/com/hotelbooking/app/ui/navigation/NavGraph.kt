package com.hotelbooking.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hotelbooking.app.ui.screens.auth.*
import com.hotelbooking.app.ui.screens.home.HomeScreen


@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
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
                onBackToLogin = { navController.popBackStack() },
                onNavigateToOTP = {
                    // Khi gửi email xong, chuyển sang trang nhập mã OTP
                    navController.navigate(Routes.VERIFY_OTP)
                }
            )
        }
        composable(Routes.VERIFY_OTP) {
            SendCodeOTPScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }
        composable(Routes.RESET_PASSWORD) {
            CreateNewPasswordScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }
        composable(Routes.HOME) {
            HomeScreen()
        }
    }
}