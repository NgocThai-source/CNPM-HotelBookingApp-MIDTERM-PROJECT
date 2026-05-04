// data/repository/AuthRepository.kt
package com.hotelbooking.app.data.repository

import com.hotelbooking.app.data.model.RegisterRequest
import com.hotelbooking.app.data.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRepository {
    @POST("api/register")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): RegisterResponse
}
