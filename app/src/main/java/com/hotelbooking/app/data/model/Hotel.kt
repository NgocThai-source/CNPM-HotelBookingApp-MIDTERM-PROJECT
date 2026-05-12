package com.hotelbooking.app.data.model

import com.google.gson.annotations.SerializedName

data class Hotel(
    @SerializedName("id") val id: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("location") val location: String = "",
    @SerializedName("image_url") val imageUrl: String = "",
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("category") val category: String = "",
    @SerializedName("badge_text") val badgeText: String? = "",
    @SerializedName("host_name") val hostName: String? = "",
    @SerializedName("host_avt_url") val hostAvatarUrl: String? = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("amenities") val amenities: List<String>? = emptyList(),
    @SerializedName("check_in_date") val checkInDate: String? = "",
    @SerializedName("check_out_date") val checkOutDate: String? = "",
    @SerializedName("is_favorite") val isFavorite: Boolean = false,
    @SerializedName("created_at") val createdAt: String? = null,
)
