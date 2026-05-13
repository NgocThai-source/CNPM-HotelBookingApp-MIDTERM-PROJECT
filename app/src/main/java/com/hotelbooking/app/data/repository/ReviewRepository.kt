package com.hotelbooking.app.data.repository

import com.hotelbooking.app.data.model.Review
import com.hotelbooking.app.data.model.ReviewListResponse
import com.hotelbooking.app.data.model.SingleReviewResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewRepository {
    @GET("api/hotels/{hotelId}/reviews")
    suspend fun getReviewsByHotelId(@Path("hotelId") hotelId: String): ReviewListResponse

    @POST("api/hotels/{hotelId}/reviews")
    suspend fun createReview(
        @Path("hotelId") hotelId: String,
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): SingleReviewResponse

    @GET("api/hotels/{hotelId}/reviews/user")
    suspend fun getUserReviewStatus(
        @Path("hotelId") hotelId: String,
        @Header("Authorization") token: String
    ): SingleReviewResponse
}
