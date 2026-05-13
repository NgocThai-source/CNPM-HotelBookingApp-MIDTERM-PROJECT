package com.hotelbooking.app.data.model

data class ReviewListResponse(
    val success: Boolean,
    val data: List<Review>? = null,
    val rating: Double? = null,
    val reviewCount: Int? = null,
    val message: String? = null
)

data class SingleReviewResponse(
    val success: Boolean,
    val data: Review? = null,
    val hasReviewed: Boolean? = null,
    val message: String? = null
)
