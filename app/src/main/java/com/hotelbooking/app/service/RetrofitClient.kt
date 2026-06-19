package com.hotelbooking.app.service

import com.hotelbooking.app.data.repository.AuthRepository
import com.hotelbooking.app.data.repository.BookingRepository
import com.hotelbooking.app.data.repository.HotelRepository
import com.hotelbooking.app.data.repository.NotificationRepository
import com.hotelbooking.app.data.repository.ReviewRepository
import com.hotelbooking.app.data.repository.RoomRepository
import com.hotelbooking.app.data.repository.SettingsRepository
import com.hotelbooking.app.util.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Auth interceptor: injects Bearer token for protected endpoints
    private val authInterceptor = Interceptor { chain ->
        val token = TokenManager.getToken()
        val request = if (!token.isNullOrEmpty()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API dành cho Đăng nhập, Đăng ký, Quên mật khẩu
    val authApi: AuthRepository by lazy {
        retrofit.create(AuthRepository::class.java)
    }

    // API dành cho Khách sạn (Lấy danh sách, tìm kiếm...)
    val hotelApi: HotelRepository by lazy {
        retrofit.create(HotelRepository::class.java)
    }

    val settingsApi: SettingsRepository by lazy {
        retrofit.create(SettingsRepository::class.java)
    }

    val bookingApi: BookingRepository by lazy {
        retrofit.create(BookingRepository::class.java)
    }

    val reviewApi: ReviewRepository by lazy {
        retrofit.create(ReviewRepository::class.java)
    }

    val notificationApi: NotificationRepository by lazy {
        retrofit.create(NotificationRepository::class.java)
    }

    val roomApi: RoomRepository by lazy {
        retrofit.create(RoomRepository::class.java)
    }

    // Giữ lại cái này để các code cũ (như màn hình Đăng nhập, OTP) đang dùng 'apiInterface' không bị báo lỗi đỏ
    val apiInterface: AuthRepository get() = authApi
}