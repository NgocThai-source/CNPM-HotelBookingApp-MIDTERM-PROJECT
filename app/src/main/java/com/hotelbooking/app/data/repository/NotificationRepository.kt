package com.hotelbooking.app.data.repository

import com.google.gson.annotations.SerializedName
import com.hotelbooking.app.data.model.GenericResponse
import com.hotelbooking.app.data.model.NotificationCountResponse
import com.hotelbooking.app.data.model.NotificationListResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
interface NotificationRepository {
    @GET("api/notifications")
    suspend fun getNotifications(@Query("user_id") userId: String): NotificationListResponse

    @POST("api/notifications")
    suspend fun createNotification(@Body request: NotificationCreateRequest): GenericResponse

    @PUT("api/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Int): GenericResponse

    @PUT("api/notifications/read-all")
    suspend fun markAllAsRead(@Query("user_id") userId: String): GenericResponse

    @DELETE("api/notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: Int): GenericResponse


    @GET("api/notifications/unread-count")
    suspend fun getUnreadCount(@Query("user_id") userId: String): NotificationCountResponse
}


data class NotificationCreateRequest(
    @SerializedName("user_id") val userId: String,
    val type: String,
    val title: String,
    val body: String
)

