package com.hotelbooking.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        // 1. Màn hình Đăng nhập
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    // Khi đăng nhập thành công, chuyển tới Home và xóa sạch lịch sử Login
                    navController.navigate(Routes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 2. Màn hình Trang chủ
        composable(Routes.HOME) {
            HomeScreen(
                onLogoutClick = {
                    // Khi đăng xuất, quay về Login và xóa sạch toàn bộ lịch sử (Backstack)
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}