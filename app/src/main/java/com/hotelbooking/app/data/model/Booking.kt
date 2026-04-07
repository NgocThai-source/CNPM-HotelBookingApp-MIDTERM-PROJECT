package com.hotelbooking.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Represents a hotel booking made by a customer.
 */
data class Booking(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val hotelId: String = "",
    val hotelName: String = "",
    val hotelImageUrl: String = "",
    val roomId: String = "",
    val roomType: String = "",
    val checkIn: Timestamp? = null,
    val checkOut: Timestamp? = null,
    val totalPrice: Double = 0.0,
    val guestCount: Int = 1,
    val status: BookingStatus = BookingStatus.PENDING,
    @ServerTimestamp
    val createdAt: Timestamp? = null
)

/**
 * Possible statuses for a booking.
 */
enum class BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED;

    companion object {
        fun fromString(value: String): BookingStatus {
            return when (value.lowercase()) {
                "confirmed" -> CONFIRMED
                "cancelled" -> CANCELLED
                "completed" -> COMPLETED
                else -> PENDING
            }
        }
    }
}
