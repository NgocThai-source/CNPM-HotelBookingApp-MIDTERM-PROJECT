package com.hotelbooking.app.data.model

import com.hotelbooking.app.ui.theme.AppColors
import androidx.compose.ui.graphics.Color

enum class NotificationType(
    val label: String,
    val iconColor: Color,
    val iconBg: Color,
    val accentBarColor: Color
) {
    BOOKING_CONFIRMED(
        label = "Booking",
        iconColor = AppColors.SkyBrand,
        iconBg = AppColors.SkyBrand.copy(alpha = 0.12f),
        accentBarColor = AppColors.SkyBrand
    ),
    CHECK_IN_REMINDER(
        label = "Check-in",
        iconColor = AppColors.SkyBrand,
        iconBg = AppColors.SkyBrand.copy(alpha = 0.12f),
        accentBarColor = AppColors.SkyBrand
    ),
    FAVORITE_ADDED(
        label = "Favorites",
        iconColor = Color(0xFFE57373),
        iconBg = Color(0xFFFFCDD2),
        accentBarColor = Color(0xFFE57373)
    ),
    PAYMENT_SUCCESS(
        label = "Payment",
        iconColor = AppColors.GoldPrimary,
        iconBg = AppColors.GoldPrimary.copy(alpha = 0.12f),
        accentBarColor = AppColors.GoldPrimary
    ),
    PROMO_OFFER(
        label = "Promo",
        iconColor = AppColors.GoldPrimary,
        iconBg = AppColors.GoldPrimary.copy(alpha = 0.12f),
        accentBarColor = AppColors.GoldPrimary
    ),
    WELCOME(
        label = "Welcome",
        iconColor = AppColors.NavyDeep,
        iconBg = AppColors.NavyDeep.copy(alpha = 0.08f),
        accentBarColor = AppColors.NavyDeep
    ),
    SYSTEM_INFO(
        label = "System",
        iconColor = AppColors.NavyMid,
        iconBg = AppColors.NavyMid.copy(alpha = 0.1f),
        accentBarColor = AppColors.NavyMid
    );

    companion object {
        fun fromString(type: String): NotificationType {
            return entries.find { it.name == type } ?: SYSTEM_INFO
        }
    }
}

data class Notification(
    val id: Int,
    val user_id: String,
    val type: String,
    val title: String,
    val body: String,
    val related_booking_id: String?,
    val is_read: Boolean,
    val created_at: String
) {
    val notificationType: NotificationType
        get() = NotificationType.fromString(type)
}

data class NotificationListResponse(
    val success: Boolean,
    val data: List<Notification>?
)

data class NotificationCountResponse(
    val success: Boolean,
    val count: Int
)

data class GenericResponse(
    val success: Boolean,
    val message: String?
)

// ──────────────────────────────────────────────────────────────
// Mock Data
// ──────────────────────────────────────────────────────────────
object MockNotifications {
    private fun today(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return sdf.format(java.util.Date())
    }

    private fun daysAgo(days: Int): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -days)
        return sdf.format(cal.time)
    }

    fun getAll(): List<Notification> = listOf(
        // ── TODAY ──────────────────────────────────────────────
        Notification(
            id = 1,
            user_id = "u1",
            type = "BOOKING_CONFIRMED",
            title = "Booking Confirmed",
            body = "Your reservation at The Ritz-Carlton is confirmed. Check-in: Dec 20.",
            related_booking_id = "b1",
            is_read = false,
            created_at = "${today()}T09:00:00"
        ),
        Notification(
            id = 2,
            user_id = "u1",
            type = "CHECK_IN_REMINDER",
            title = "Check-in Tomorrow",
            body = "Don't forget! Your stay at Marina Bay Hotel starts tomorrow at 2:00 PM.",
            related_booking_id = "b2",
            is_read = false,
            created_at = "${today()}T14:30:00"
        ),
        Notification(
            id = 3,
            user_id = "u1",
            type = "FAVORITE_ADDED",
            title = "Added to Favorites",
            body = "Sunset Beach Resort was added to your wishlist. Price dropped 15%!",
            related_booking_id = null,
            is_read = false,
            created_at = "${today()}T11:15:00"
        ),

        // ── YESTERDAY ────────────────────────────────────────
        Notification(
            id = 4,
            user_id = "u1",
            type = "PAYMENT_SUCCESS",
            title = "Payment Successful",
            body = "₫2,500,000 charged to your card ending ••4567. Booking #HB-2024-8821.",
            related_booking_id = "b3",
            is_read = true,
            created_at = "${daysAgo(1)}T16:45:00"
        ),
        Notification(
            id = 5,
            user_id = "u1",
            type = "PROMO_OFFER",
            title = "20% Off Weekend Stays",
            body = "Use code WEEKEND20 at checkout. Valid on all hotels until Dec 31, 2024.",
            related_booking_id = null,
            is_read = false,
            created_at = "${daysAgo(1)}T10:00:00"
        ),
        Notification(
            id = 6,
            user_id = "u1",
            type = "BOOKING_CONFIRMED",
            title = "Booking Confirmed",
            body = "You have successfully booked Grand Hyatt. Confirmation sent to email.",
            related_booking_id = "b4",
            is_read = true,
            created_at = "${daysAgo(1)}T08:20:00"
        ),

        // ── EARLIER ───────────────────────────────────────────
        Notification(
            id = 7,
            user_id = "u1",
            type = "WELCOME",
            title = "Welcome to HotelApp",
            body = "Explore luxury stays and exclusive deals. Start your journey today!",
            related_booking_id = null,
            is_read = true,
            created_at = "${daysAgo(3)}T08:00:00"
        ),
        Notification(
            id = 8,
            user_id = "u1",
            type = "SYSTEM_INFO",
            title = "App Updated",
            body = "HotelApp v2.1 is here with a redesigned booking flow and faster search.",
            related_booking_id = null,
            is_read = true,
            created_at = "${daysAgo(5)}T09:30:00"
        ),
        Notification(
            id = 9,
            user_id = "u1",
            type = "FAVORITE_ADDED",
            title = "Price Drop Alert",
            body = "Alila Villas Uluwatu just dropped 20%! Save up to ₫1,800,000.",
            related_booking_id = null,
            is_read = true,
            created_at = "${daysAgo(7)}T12:00:00"
        ),
        Notification(
            id = 10,
            user_id = "u1",
            type = "PROMO_OFFER",
            title = "Member Exclusive Deal",
            body = "Enjoy triple points on your next booking. Valid for Gold members only.",
            related_booking_id = null,
            is_read = true,
            created_at = "${daysAgo(10)}T15:00:00"
        )
    )

    fun getUnreadCount(): Int = getAll().count { !it.is_read }
}
