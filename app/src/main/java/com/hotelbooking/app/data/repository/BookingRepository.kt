package com.hotelbooking.app.data.repository

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query // 👈 Đã thêm import này

data class BookingSubmitRequest(
    @SerializedName("userId") val userId: String, // 👈 THÊM DÒNG NÀY ĐỂ FIX LỖI BOOKING VIEW MODEL
    @SerializedName("hotelId") val hotelId: String,
    @SerializedName("hotelTitle") val hotelTitle: String,
    @SerializedName("hotelImageUrl") val hotelImageUrl: String,
    @SerializedName("guestName") val guestName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("checkInDate") val checkInDate: String,
    @SerializedName("checkOutDate") val checkOutDate: String,
    @SerializedName("numberOfNights") val numberOfNights: Int,
    @SerializedName("guestCount") val guestCount: Int,
    @SerializedName("pricePerNight") val pricePerNight: Double,
    @SerializedName("totalPrice") val totalPrice: Double,
    @SerializedName("exchangeRate") val exchangeRate: Double
)

data class BookingSubmitResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: BookingItem? = null
)

data class BookingListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<BookingItem>? = null
)

data class BookingItem(
    @SerializedName("booking_id") val bookingId: String,
    @SerializedName("hotel_id") val hotelId: String,
    @SerializedName("hotel_title") val hotelTitle: String,
    @SerializedName("hotel_image_url") val hotelImageUrl: String,
    @SerializedName("guest_name") val guestName: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("check_in_date") val checkInDate: String,
    @SerializedName("check_out_date") val checkOutDate: String,
    @SerializedName("number_of_nights") val numberOfNights: Int,
    @SerializedName("guest_count") val guestCount: Int,
    @SerializedName("price_per_night") val pricePerNight: Double,
    @SerializedName("total_price") val totalPrice: Double,
    @SerializedName("payment_status") val paymentStatus: String,
    @SerializedName("paid_at") val paidAt: String?,
    @SerializedName("created_at") val createdAt: String
)

interface BookingRepository {
    @POST("api/bookings")
    suspend fun submitBooking(@Body request: BookingSubmitRequest): BookingSubmitResponse

    // 👈 THÊM @Query("userId") ĐỂ FIX LỖI MY BOOKINGS VIEW MODEL
    @GET("api/bookings")
    suspend fun getBookings(@Query("userId") userId: String): BookingListResponse

    @PUT("api/bookings/{bookingId}/pay")
    suspend fun markAsPaid(@Path("bookingId") bookingId: String): BookingSubmitResponse
}