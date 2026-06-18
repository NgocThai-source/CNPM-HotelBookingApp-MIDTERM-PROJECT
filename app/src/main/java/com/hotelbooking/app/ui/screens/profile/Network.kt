package com.hotelbooking.app.ui.screens.profile

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

// Model dữ liệu khớp với cấu trúc trong database và BE
data class Hotel(
    val id: Int,
    val name: String,
    val location: String,
    val price: String,
    val rating: Double
)

data class WishlistResponse(
    val success: Boolean,
    val data: List<Hotel>
)

data class ProfileResponse(
    val success: Boolean,
    val data: UserProfileDto?
)

data class UserProfileDto(
    val id: String,
    val full_name: String?,
    val email: String?,
    val phone: String?
)
data class UpdateProfileRequest(
    val userId: String,
    val full_name: String,
    val phone: String
)



// Interface kết nối với các Route Node.js đã làm
interface ApiService {
    @GET("api/users/profile/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): retrofit2.Response<ProfileResponse>

    @GET("api/users/favorites/{userId}")
    suspend fun getWishlist(@Path("userId") userId: String): retrofit2.Response<WishlistResponse>

    @PUT("api/users/update")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): retrofit2.Response<Map<String, Any>>

    @DELETE("api/users/favorites/{userId}/{hotelId}")
    suspend fun removeFromWishlist(
        @Path("userId") userId: String,
        @Path("hotelId") hotelId: Int
    ): retrofit2.Response<Map<String, Any>>
}

object RetrofitInstance {
    // Port 3000 khớp với server Node.js của bạn
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}