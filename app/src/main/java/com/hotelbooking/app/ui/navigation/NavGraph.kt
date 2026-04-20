package com.hotelbooking.app.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hotelbooking.app.ui.screens.auth.ForgotPasswordScreen
import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

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

        // 2. Nhánh Quên mật khẩu
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onSendClick = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
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