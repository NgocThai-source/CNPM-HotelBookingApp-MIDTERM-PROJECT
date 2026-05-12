package com.hotelbooking.app.service

import com.hotelbooking.app.data.repository.AuthRepository
import com.hotelbooking.app.data.repository.HotelRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthRepository by lazy {
        retrofit.create(AuthRepository::class.java)
    }

    val hotelApi: HotelRepository by lazy {
        retrofit.create(HotelRepository::class.java)
    }

    val apiInterface: AuthRepository get() = authApi
}
