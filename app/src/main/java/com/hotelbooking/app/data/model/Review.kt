package com.hotelbooking.app.data.model

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id") val id: String = "",
    @SerializedName("hotel_id") val hotelId: String = "",
    @SerializedName("user_id") val userId: String = "",
    @SerializedName("user_name") val userName: String = "",
    @SerializedName("user_avatar_url") val userAvatarUrl: String? = "",
    @SerializedName("content") val content: String = "",
    @SerializedName("created_at") val createdAt: String = ""
)
