package com.hotelbooking.app.data.model

import com.google.gson.annotations.SerializedName

data class Hotel(
    @SerializedName("_id") val id: String = "",
    val title: String = "",
    val location: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,
    val rating: Double = 0.0,
    val category: String = "",
    val hostName: String = "",
    val badgeText: String = "",
    val hostAvatarUrl: String = "",
    val description: String = "" // Dữ liệu động
)
