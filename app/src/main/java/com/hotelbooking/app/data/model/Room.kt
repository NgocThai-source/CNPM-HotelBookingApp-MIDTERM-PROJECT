package com.hotelbooking.app.data.model

import com.google.firebase.firestore.DocumentId

/**
 * Represents a room within a hotel.
 * Stored as a subcollection under hotels/{hotelId}/rooms.
 */
data class Room(
    @DocumentId
    val id: String = "",
    val type: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val capacity: Int = 2,
    val isAvailable: Boolean = true,
    val imageUrl: String = "",
    val amenities: List<String> = emptyList()
)
