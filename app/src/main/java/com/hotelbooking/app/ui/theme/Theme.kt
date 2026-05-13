package com.hotelbooking.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * ============================================================
 * Material3 Theme Configuration
 * ============================================================
 */
private val DarkColorScheme = darkColorScheme(
    primary = AppColors.CyanMain,
    onPrimary = AppColors.TextDark,
    secondary = AppColors.CyanDark,
    background = AppColors.DarkBackground,
    surface = AppColors.DarkSurface,
    onBackground = AppColors.TextOnDark,
    onSurface = AppColors.TextOnDark,
    error = AppColors.Error
)

private val LightColorScheme = lightColorScheme(
    primary = AppColors.CyanMain,
    onPrimary = AppColors.TextDark,
    secondary = AppColors.CyanDark,
    background = AppColors.LightBackground,
    surface = AppColors.LightSurface,
    onBackground = AppColors.TextDark,
    onSurface = AppColors.TextDark,
    error = AppColors.Error
)

@Composable
fun HotelBookingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = AppTypography,
        content = content
    )
}