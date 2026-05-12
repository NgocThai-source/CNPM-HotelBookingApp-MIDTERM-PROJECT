package com.hotelbooking.app.ui.screens.profile

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// 1. Data classes hứng dữ liệu từ JSON của Node.js
data class ProfileResponse(
    val success: Boolean,
    val data: ProfileUser
)

data class ProfileUser(
    val id: String,
    val full_name: String,
    val phone: String,
    val email: String,
    val created_at: String
)

// 2. ApiService cấu hình đường dẫn gọi API
interface ApiService {
    @GET("api/users/{id}")
    suspend fun getUserProfile(@Path("id") userId: String): ProfileResponse

    companion object {
        // Dùng 10.0.2.2 để máy ảo Android gọi xuống localhost của máy tính
        private const val BASE_URL = "http://10.0.2.2:3000/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}