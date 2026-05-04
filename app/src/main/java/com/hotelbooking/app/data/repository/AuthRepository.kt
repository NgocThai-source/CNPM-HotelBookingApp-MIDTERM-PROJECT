package com.hotelbooking.app.data.repository

import com.hotelbooking.app.data.model.ForgotPasswordRequest
import com.hotelbooking.app.data.model.ForgotPasswordResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRepository {
    @POST("api/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): ForgotPasswordResponse
}