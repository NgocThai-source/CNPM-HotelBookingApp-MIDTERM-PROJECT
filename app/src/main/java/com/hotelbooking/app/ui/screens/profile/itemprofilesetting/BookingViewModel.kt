package com.hotelbooking.app.ui.screens.profile.itemprofilesetting

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingViewModel : ViewModel() {
    val bookingList = mutableStateListOf<BookingModel>()
    val isLoading = mutableStateOf(false)

    fun fetchBookings(userId: Int) {
        isLoading.value = true
        RetrofitClient.instance.getBookings(userId).enqueue(object : Callback<List<BookingModel>> {
            override fun onResponse(call: Call<List<BookingModel>>, response: Response<List<BookingModel>>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    bookingList.clear()
                    response.body()?.let { bookingList.addAll(it) }
                }
            }

            override fun onFailure(call: Call<List<BookingModel>>, t: Throwable) {
                isLoading.value = false
                t.printStackTrace()
            }
        })
    }
}