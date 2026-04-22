package com.hotelbooking.app.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hotelbooking.app.ui.screens.auth.ForgotPasswordScreen
import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.auth.OtpVerificationScreen
import com.hotelbooking.app.ui.screens.auth.ResetPasswordScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    val animationDuration = 850
    val easingCurve = FastOutSlowInEasing

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeIn(animationSpec = tween(animationDuration)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeOut(animationSpec = tween(animationDuration)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeIn(animationSpec = tween(animationDuration)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth / 4 }, animationSpec = tween(animationDuration, easing = easingCurve)) + fadeOut(animationSpec = tween(animationDuration)) }
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = { navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } } },
                onNavigateToRegister = {
                    // Nút đăng ký vẫn hiển thị trên UI, nhưng ở nhánh này bấm vào sẽ không làm gì cả.
                },
                onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        // Đã xóa hoàn toàn composable(Routes.REGISTER) để không bị lỗi parameter

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onSendClick = { enteredEmail ->
                    val encodedEmail = URLEncoder.encode(enteredEmail, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Routes.VERIFY_OTP}/$encodedEmail")
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.VERIFY_OTP}/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val rawEmail = backStackEntry.arguments?.getString("email") ?: ""
            val decodedEmail = URLDecoder.decode(rawEmail, StandardCharsets.UTF_8.toString())

            OtpVerificationScreen(
                email = decodedEmail,
                onVerifySuccess = { navController.navigate(Routes.RESET_PASSWORD) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.RESET_PASSWORD) {
            ResetPasswordScreen(
                onResetSuccess = {
                    navController.navigate(Routes.LOGIN) { popUpTo(Routes.LOGIN) { inclusive = true } }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(onLogoutClick = { navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } })
        }
    }
}