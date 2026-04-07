package com.hotelbooking.app.util

/**
 * Application-wide constants.
 */
object Constants {
    // Firestore collection names
    const val COLLECTION_USERS = "users"
    const val COLLECTION_HOTELS = "hotels"
    const val COLLECTION_ROOMS = "rooms"
    const val COLLECTION_BOOKINGS = "bookings"
    const val COLLECTION_MESSAGES = "messages"

    // User roles
    const val ROLE_CUSTOMER = "customer"
    const val ROLE_MANAGER = "manager"

    // Booking statuses
    const val STATUS_PENDING = "pending"
    const val STATUS_CONFIRMED = "confirmed"
    const val STATUS_CANCELLED = "cancelled"
    const val STATUS_COMPLETED = "completed"

    // Navigation argument keys
    const val ARG_HOTEL_ID = "hotelId"
    const val ARG_BOOKING_ID = "bookingId"
    const val ARG_CHAT_ID = "chatId"
    const val ARG_USER_ID = "userId"

    // Date format
    const val DATE_FORMAT_DISPLAY = "dd/MM/yyyy"
    const val DATE_FORMAT_FULL = "dd/MM/yyyy HH:mm"
}
