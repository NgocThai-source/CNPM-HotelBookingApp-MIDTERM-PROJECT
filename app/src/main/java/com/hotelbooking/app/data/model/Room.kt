package com.hotelbooking.app.data.model

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("id") val id: String = "",
    @SerializedName("hotel_id") val hotelId: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("price_per_night") val pricePerNight: Double = 0.0,
    @SerializedName("capacity") val capacity: Int = 1,
    @SerializedName("total_quantity") val totalQuantity: Int = 0,
    @SerializedName("available_quantity") val availableQuantity: Int = 0,
    @SerializedName("description") val description: String? = null,
    @SerializedName("amenities") val amenities: List<String>? = emptyList(),
    @SerializedName("images") val images: List<String>? = emptyList(),
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("room_name") val roomName: String? = null,
    @SerializedName("room_type") val roomType: String? = null,
    @SerializedName("check_in_date") val checkInDate: String? = null,
    @SerializedName("check_out_date") val checkOutDate: String? = null,
    @SerializedName("room_count") val roomCount: Int? = null,
)
