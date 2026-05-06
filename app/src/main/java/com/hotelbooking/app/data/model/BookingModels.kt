package com.hotelbooking.app.data.model

/**
 * Data models for Booking management.
 * These are used for backend communication in BookingScreen.
 */

data class BookingResponse(
    val success: Boolean,
    val message: String,
    val data: List<BookingData>? = null
)

data class BookingData(
    val bookingId: String,
    val hotelId: String,
    val hotelName: String,
    val location: String,
    val imageUrl: String,
    val checkInDate: String,
    val checkOutDate: String,
    val guestsCount: Int,
    val roomsCount: Int,
    val totalPrice: Double,
    val status: String, // "Upcoming", "Ongoing", "Completed", "Cancelled"
    val rating: Double,
    val roomType: String
)

data class CancelBookingRequest(
    val bookingId: String,
    val reason: String? = null
)

data class CancelBookingResponse(
    val success: Boolean,
    val message: String
)
