package com.hotelbooking.app.data.repository

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

data class SettingsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: Map<String, String>? = null
)

interface SettingsRepository {
    @GET("api/settings")
    suspend fun getSettings(): SettingsResponse
}
