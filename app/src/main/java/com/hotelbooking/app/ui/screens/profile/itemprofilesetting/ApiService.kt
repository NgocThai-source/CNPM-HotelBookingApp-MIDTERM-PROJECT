package com.hotelbooking.app.ui.screens.profile.itemprofilesetting

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/bookings/{userId}")
    fun getBookings(@Path("userId") userId: Int): Call<List<BookingModel>>
    // Vì cùng thư mục nên không cần dòng import BookingModel nữa
}