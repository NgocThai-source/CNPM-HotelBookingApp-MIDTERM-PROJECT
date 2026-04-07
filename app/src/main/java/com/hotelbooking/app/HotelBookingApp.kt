package com.hotelbooking.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Hotel Booking App.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class HotelBookingApp : Application()
