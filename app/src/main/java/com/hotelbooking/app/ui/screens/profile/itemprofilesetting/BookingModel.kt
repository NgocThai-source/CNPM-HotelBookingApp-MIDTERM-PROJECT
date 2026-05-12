package com.hotelbooking.app.ui.screens.profile.itemprofilesetting

data class BookingModel(
    val id: Int,
    val check_in_date: String,
    val check_out_date: String,
    val total_price: Double,
    val status: String
)