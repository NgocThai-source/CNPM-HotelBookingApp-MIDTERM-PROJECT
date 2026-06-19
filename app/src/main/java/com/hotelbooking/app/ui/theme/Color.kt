package com.hotelbooking.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ============================================================
 * App Design System – Color Palette
 * Premium Hotel Booking Theme with Cyan accent
 * ============================================================
 */
object AppColors {
    // ── Luxury Hotel Palette ──
    val NavyPrimary = Color(0xFF1C2B4A)
    val NavyLight = Color(0xFF2B4275)
    val GoldAccent = Color(0xFFC4973F)
    val GoldLight = Color(0xFFE8C97A)
    val IvoryBg = Color(0xFFFAF8F5)
    val WarmBorder = Color(0xFFE8E4DE)
    val HeroGradientTop = Color(0xCC1C2B4A)
    val HeroGradientMid = Color(0x661C2B4A)

    // ── Design System Premium Palette (spec-aligned) ──
    // Deep Navy gradient (#0A1730 → #16315F)
    val NavyDeep = Color(0xFF0A1730)
    val NavyMid = Color(0xFF16315F)
    val NavySurface = Color(0xFF1E3A5F)

    // Brand Sky (#2EB8E6) — primary brand accent
    val SkyBrand = Color(0xFF2EB8E6)
    val SkyLight = Color(0xFF6AD4F5)
    val SkyDark = Color(0xFF1A9FC0)

    // Design System Gold (#E4B264)
    val GoldPrimary = Color(0xFFE4B264)
    val GoldDark = Color(0xFFCE9A45)
    val GoldLightCustom = Color(0xFFF0C87A)

    // Warm Cream (#F7F3EC) — warm mood matching Auth sunset
    val CreamSurface = Color(0xFFF7F3EC)
    val CreamLight = Color(0xFFFAF6EF)
    val CreamDark = Color(0xFFEDE6DA)

    // Glass / Frosted surface tokens
    val GlassWhite = Color(0xF5FFFFFF)
    val GlassStroke = Color(0x33FFFFFF)
    val GlassBlur = Color(0x1AFFFFFF)

    // ── Primary Brand Colors ──
    val CyanMain = Color(0xFF00E5FF)
    val CyanLight = Color(0xFF6EFFFF)
    val CyanDark = Color(0xFF00B8D4)
    val CyanSurface = Color(0xFFE0F7FA)
    val CyanSubtle = Color(0xFFF0FDFF)

    // ── Gold Accent (for premium touches) ──
    val Gold = Color(0xFFFFC107)
    val GoldDarker = Color(0xFFCA8A04)

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

    // ── Premium Auth Hero Tokens ──
    // Navy used to anchor the top of the hero overlay – brand-consistent with the logo.
    val Navy = Color(0xFF0B1E33)
    val DeepNavy = Color(0xFF071324)

    // Hero overlays – top is dark and translucent, bottom blends into surface.
    val HeroOverlayTop = Color(0xCC0B1E33)
    val HeroOverlayMid = Color(0x660B1E33)
    val HeroOverlayLight = Color(0xF2F5F7FA)
    val HeroOverlayDark = Color(0xF20F1117)

    // Glass tokens – frosted card backgrounds.
    val GlassLight = Color(0xF2FFFFFF)
    val GlassDark = Color(0xF21A1D28)
    val GlassStrokeLight = Color(0x33FFFFFF)
    val GlassStrokeDark = Color(0x33FFFFFF)

    fun heroOverlayBottom(dark: Boolean) = if (dark) HeroOverlayDark else HeroOverlayLight
    fun glass(dark: Boolean) = if (dark) GlassDark else GlassLight
    fun glassStroke(dark: Boolean) = if (dark) GlassStrokeDark else GlassStrokeLight

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