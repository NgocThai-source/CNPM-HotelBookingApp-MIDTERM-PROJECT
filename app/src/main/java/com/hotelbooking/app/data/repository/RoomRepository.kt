package com.hotelbooking.app.data.repository

import com.google.gson.annotations.SerializedName
import com.hotelbooking.app.data.model.Room
import retrofit2.http.GET
import retrofit2.http.Query

data class RoomListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<Room>? = null
)

interface RoomRepository {
    @GET("api/rooms")
    suspend fun getRoomsByHotelId(@Query("hotel_id") hotelId: String): RoomListResponse
}
