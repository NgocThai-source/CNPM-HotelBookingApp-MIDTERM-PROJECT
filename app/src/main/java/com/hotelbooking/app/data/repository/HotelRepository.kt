package com.hotelbooking.app.data.repository

import com.hotelbooking.app.data.model.Hotel
import retrofit2.http.GET
import retrofit2.http.Path

interface HotelRepository {
    @GET("api/hotels")
    suspend fun getAllHotels(): List<Hotel>

    @GET("api/hotels/{id}")
    suspend fun getHotelById(@Path("id") id: String): Hotel
}
