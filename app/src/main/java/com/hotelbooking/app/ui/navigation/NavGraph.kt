package com.hotelbooking.app.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen
import com.hotelbooking.app.ui.screens.profile.ProfileScreen
import com.hotelbooking.app.ui.screens.profile.Routes
// QUAN TRỌNG: Import màn hình Screen chứ không phải Item
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.BookingHistoryScreen
import com.hotelbooking.app.ui.screens.profile.itemprofilesetting.PaymentItem

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val animationDuration = 850
    val easingCurve = FastOutSlowInEasing

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
                onLoginClick = {
                    navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(navController)
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }

        // SỬA TẠI ĐÂY:
        composable(Routes.BOOKING_HISTORY) {
            BookingHistoryScreen(
                navController = navController
            )
        }

        composable(Routes.PAYMENT_METHOD) {
            PaymentItem(navController = navController)
        }
    }
}

@Composable
fun EditProfileScreen(navController: NavController) {
    Text("Edit Profile Screen")
}