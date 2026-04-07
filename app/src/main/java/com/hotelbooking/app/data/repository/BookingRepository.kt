package com.hotelbooking.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hotelbooking.app.data.model.Booking
import com.hotelbooking.app.data.model.BookingStatus
import com.hotelbooking.app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for booking operations.
 * Handles creating, updating, cancelling, and querying bookings in Firestore.
 */
@Singleton
class BookingRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    /**
     * Create a new booking.
     */
    suspend fun createBooking(booking: Booking): Result<String> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_BOOKINGS)
                .add(booking)
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get bookings for a specific user (customer), real-time.
     */
    fun getUserBookingsFlow(userId: String): Flow<List<Booking>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_BOOKINGS)
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val bookings = snapshot?.toObjects(Booking::class.java) ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Get bookings for a specific hotel (for managers), real-time.
     */
    fun getHotelBookingsFlow(hotelId: String): Flow<List<Booking>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_BOOKINGS)
            .whereEqualTo("hotelId", hotelId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val bookings = snapshot?.toObjects(Booking::class.java) ?: emptyList()
                trySend(bookings)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Update booking status (confirm, cancel, complete).
     */
    suspend fun updateBookingStatus(
        bookingId: String,
        status: BookingStatus
    ): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_BOOKINGS)
                .document(bookingId)
                .update("status", status.name)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cancel a booking.
     */
    suspend fun cancelBooking(bookingId: String): Result<Unit> {
        return updateBookingStatus(bookingId, BookingStatus.CANCELLED)
    }

    /**
     * Get a single booking by ID.
     */
    suspend fun getBookingById(bookingId: String): Result<Booking> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_BOOKINGS)
                .document(bookingId)
                .get()
                .await()
            val booking = snapshot.toObject(Booking::class.java)
                ?: return Result.failure(Exception("Booking not found"))
            Result.success(booking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
