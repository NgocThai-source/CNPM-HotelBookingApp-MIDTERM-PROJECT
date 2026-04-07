package com.hotelbooking.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Represents a hotel listing in the system.
 */
data class Hotel(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val city: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val imageUrls: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val priceRange: PriceRange = PriceRange(),
    val managerId: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null
)

/**
 * Price range for a hotel (min and max price per night).
 */
data class PriceRange(
    val min: Double = 0.0,
    val max: Double = 0.0
)
