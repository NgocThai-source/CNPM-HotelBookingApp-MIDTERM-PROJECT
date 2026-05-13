package com.hotelbooking.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ============================================================
 * App Design System – Color Palette
 * Premium Hotel Booking Theme with Cyan accent
 * ============================================================
 */
object AppColors {
    // ── Primary Brand Colors ──
    val CyanMain = Color(0xFF00E5FF)
    val CyanLight = Color(0xFF6EFFFF)
    val CyanDark = Color(0xFF00B8D4)
    val CyanSurface = Color(0xFFE0F7FA)
    val CyanSubtle = Color(0xFFF0FDFF)

    // ── Gold Accent (for premium touches) ──
    val Gold = Color(0xFFFFC107)
    val GoldDark = Color(0xFFCA8A04)

    // ── Status Colors ──
    val Success = Color(0xFF4CAF50)
    val Error = Color(0xFFEF5350)
    val Warning = Color(0xFFFF9800)

    // ── Dark Mode Palette ──
    val DarkBackground = Color(0xFF0F1117)
    val DarkSurface = Color(0xFF1A1D28)
    val DarkCard = Color(0xFF222639)
    val DarkBorder = Color(0xFF2E3348)
    val DarkElevated = Color(0xFF292D3E)

    // ── Light Mode Palette ──
    val LightBackground = Color(0xFFF5F7FA)
    val LightSurface = Color(0xFFFFFFFF)
    val LightCard = Color(0xFFFFFFFF)
    val LightBorder = Color(0xFFE8ECF1)

    // ── Text Colors ──
    val TextDark = Color(0xFF0F172A)
    val TextMedium = Color(0xFF475569)
    val TextLight = Color(0xFF94A3B8)
    val TextOnDark = Color(0xFFF8FAFC)
    val TextOnDarkSecondary = Color(0xFFCBD5E1)

    // ── Adaptive helpers ──
    fun background(dark: Boolean) = if (dark) DarkBackground else LightBackground
    fun surface(dark: Boolean) = if (dark) DarkSurface else LightSurface
    fun card(dark: Boolean) = if (dark) DarkCard else LightCard
    fun border(dark: Boolean) = if (dark) DarkBorder else LightBorder
    fun textPrimary(dark: Boolean) = if (dark) TextOnDark else TextDark
    fun textSecondary(dark: Boolean) = if (dark) TextOnDarkSecondary else TextMedium
    fun textTertiary(dark: Boolean) = if (dark) Color(0xFF64748B) else TextLight
}