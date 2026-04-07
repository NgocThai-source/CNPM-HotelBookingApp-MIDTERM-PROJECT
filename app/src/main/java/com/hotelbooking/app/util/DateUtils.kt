package com.hotelbooking.app.util

import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Extension and utility functions for date formatting.
 */
object DateUtils {
    /**
     * Format a Firebase Timestamp to a display string.
     */
    fun Timestamp.toDisplayString(): String {
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale("vi", "VN"))
        return sdf.format(this.toDate())
    }

    /**
     * Format a Firebase Timestamp to a full date-time string.
     */
    fun Timestamp.toFullString(): String {
        val sdf = SimpleDateFormat(Constants.DATE_FORMAT_FULL, Locale("vi", "VN"))
        return sdf.format(this.toDate())
    }

    /**
     * Calculate the number of nights between two timestamps.
     */
    fun calculateNights(checkIn: Timestamp, checkOut: Timestamp): Long {
        val diff = checkOut.toDate().time - checkIn.toDate().time
        return TimeUnit.MILLISECONDS.toDays(diff)
    }

    /**
     * Convert a Date to Firebase Timestamp.
     */
    fun Date.toTimestamp(): Timestamp = Timestamp(this)
}

/**
 * Extension functions for formatting prices.
 */
fun Double.toCurrencyString(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(this)
}

/**
 * Extension function for relative time display (e.g., "5 phút trước").
 */
fun Timestamp?.toRelativeTime(): String {
    if (this == null) return ""
    val now = System.currentTimeMillis()
    val diff = now - this.toDate().time
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        minutes < 1 -> "Vừa xong"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        days < 7 -> "$days ngày trước"
        else -> this.toDate().let {
            SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale("vi", "VN")).format(it)
        }
    }
}
