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
import com.hotelbooking.app.ui.screens.booking.BookingFormScreen
import com.hotelbooking.app.ui.screens.booking.MyBookingsScreen
import com.hotelbooking.app.ui.screens.detail.HotelDetailScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen
import com.hotelbooking.app.ui.screens.payment.PaymentScreen

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

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("hotelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            val detailViewModel: com.hotelbooking.app.ui.screens.detail.HotelDetailViewModel = viewModel(backStackEntry)
            HotelDetailScreen(
                navController = navController,
                hotelId = hotelId,
                isDarkMode = isDarkMode,
                viewModel = detailViewModel
            )
        }

        composable(
            route = Routes.BOOKING,
            arguments = listOf(
                navArgument("hotelId") { type = NavType.StringType },
                navArgument("hotelTitle") { type = NavType.StringType },
                navArgument("hotelPrice") { type = NavType.StringType },
                navArgument("hotelImageUrl") { type = NavType.StringType },
                navArgument("checkInAvailable") { type = NavType.StringType },
                navArgument("checkOutAvailable") { type = NavType.StringType },
                navArgument("exchangeRate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            BookingFormScreen(
                navController = navController,
                hotelId = args?.getString("hotelId") ?: "",
                hotelTitle = Routes.decodeHotelTitle(args?.getString("hotelTitle") ?: ""),
                hotelPrice = args?.getString("hotelPrice")?.toDoubleOrNull() ?: 0.0,
                hotelImageUrl = Routes.decodeImageUrl(args?.getString("hotelImageUrl") ?: ""),
                checkInAvailable = args?.getString("checkInAvailable") ?: "",
                checkOutAvailable = args?.getString("checkOutAvailable") ?: "",
                exchangeRate = args?.getString("exchangeRate")?.toDoubleOrNull() ?: 26000.0
            )
        }

        composable(
            route = Routes.PAYMENT,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType },
                navArgument("hotelName") { type = NavType.StringType },
                navArgument("hotelImageUrl") { type = NavType.StringType },
                navArgument("guestName") { type = NavType.StringType },
                navArgument("phone") { type = NavType.StringType },
                navArgument("totalPriceUSD") { type = NavType.StringType },
                navArgument("totalPriceVND") { type = NavType.StringType },
                navArgument("checkInDate") { type = NavType.StringType },
                navArgument("checkOutDate") { type = NavType.StringType },
                navArgument("numberOfNights") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments
            PaymentScreen(
                navController = navController,
                bookingId = args?.getString("bookingId") ?: "",
                hotelName = Routes.decodeParam(args?.getString("hotelName") ?: ""),
                hotelImageUrl = Routes.decodeParam(args?.getString("hotelImageUrl") ?: ""),
                guestName = Routes.decodeParam(args?.getString("guestName") ?: ""),
                phone = args?.getString("phone") ?: "",
                totalPriceUSD = args?.getString("totalPriceUSD")?.toDoubleOrNull() ?: 0.0,
                totalPriceVND = args?.getString("totalPriceVND")?.toLongOrNull() ?: 0L,
                checkInDate = Routes.decodeParam(args?.getString("checkInDate") ?: ""),
                checkOutDate = Routes.decodeParam(args?.getString("checkOutDate") ?: ""),
                numberOfNights = args?.getString("numberOfNights")?.toIntOrNull() ?: 1
            )
        }

        composable(Routes.MY_BOOKINGS) {
            MyBookingsScreen(
                navController = navController,
                isDarkMode = isDarkMode
            )
        }
    }
}
