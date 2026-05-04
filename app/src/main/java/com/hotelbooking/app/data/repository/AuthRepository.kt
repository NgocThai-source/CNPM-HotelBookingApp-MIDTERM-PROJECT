package com.hotelbooking.app.data.repository

import com.hotelbooking.app.data.model.*
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRepository {
    // API Đăng ký
    @POST("api/register")
    suspend fun registerUser(@Body registerRequest: RegisterRequest): RegisterResponse

    // API Đăng nhập và trả về token
    @POST("api/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): LoginResponse
}