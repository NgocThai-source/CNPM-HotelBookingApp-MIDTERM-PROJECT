package com.hotelbooking.app.data.repository

import com.hotelbooking.app.data.model.ForgotPasswordRequest
import com.hotelbooking.app.data.model.VerifyResetRequest
import com.hotelbooking.app.data.model.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthRepository {
    @POST("api/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): AuthResponse

    @POST("api/verify-and-reset-password")
    suspend fun verifyAndResetPassword(@Body request: VerifyResetRequest): AuthResponse
}


