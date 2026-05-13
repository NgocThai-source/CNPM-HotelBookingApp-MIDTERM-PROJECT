package com.hotelbooking.app.data.repository

import com.hotelbooking.app.data.model.Hotel
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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

data class FavoriteResponse(
    val success: Boolean,
    val message: String
)

interface HotelRepository {
    @GET("api/hotels")
    suspend fun getAllHotels(): HotelListResponse

    @GET("api/hotels/{id}")
    suspend fun getHotelById(@Path("id") id: String): HotelDetailResponse

    // Favorites endpoints
    @GET("api/favorites")
    suspend fun getFavorites(): HotelListResponse

    @POST("api/favorites/{hotelId}")
    suspend fun addFavorite(@Path("hotelId") hotelId: String): FavoriteResponse

    @DELETE("api/favorites/{hotelId}")
    suspend fun removeFavorite(@Path("hotelId") hotelId: String): FavoriteResponse
}
