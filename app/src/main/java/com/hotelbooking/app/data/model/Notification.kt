package com.hotelbooking.app.data.model

data class Notification(
    val id: Int,
    val user_id: String,
    val type: String,
    val title: String,
    val body: String,
    val related_booking_id: String?,
    val is_read: Boolean,
    val created_at: String
)

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
