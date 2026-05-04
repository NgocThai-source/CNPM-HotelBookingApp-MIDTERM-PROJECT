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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hotelbooking.app.ui.screens.auth.LoginScreen
import com.hotelbooking.app.ui.screens.home.HomeScreen
import com.hotelbooking.app.ui.screens.detail.HotelDetailScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val animationDuration = 850
    val easingCurve = FastOutSlowInEasing

    // Biến trạng thái dùng chung toàn app, lưu được khi xoay màn hình
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
                onLoginClick = {
                    navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } }
                },
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
            arguments = listOf(navArgument("hotelName") { type = NavType.StringType })
        ) { backStackEntry ->
            val hotelName = backStackEntry.arguments?.getString("hotelName") ?: ""
            HotelDetailScreen(
                navController = navController,
                hotelName = hotelName,
                isDarkMode = isDarkMode
            )
        }
    }
}