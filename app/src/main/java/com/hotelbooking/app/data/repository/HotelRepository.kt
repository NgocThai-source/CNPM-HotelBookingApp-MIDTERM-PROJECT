package com.hotelbooking.app.data.repository

import com.hotelbooking.app.data.model.Hotel
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API response wrapper from the backend.
 * Backend returns: { "success": true, "data": [...] }
 */
data class HotelListResponse(
    val success: Boolean,
    val data: List<Hotel>? = null
)

data class HotelDetailResponse(
    val success: Boolean,
    val data: Hotel
)

interface HotelRepository {
    @GET("api/hotels")
    suspend fun getAllHotels(): HotelListResponse

    @GET("api/hotels/{id}")
    suspend fun getHotelById(@Path("id") id: String): HotelDetailResponse
}
