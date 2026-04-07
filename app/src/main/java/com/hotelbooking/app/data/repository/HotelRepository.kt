package com.hotelbooking.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.data.model.Room
import com.hotelbooking.app.util.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for hotel and room operations.
 * Handles CRUD for hotels and room subcollections in Firestore.
 */
@Singleton
class HotelRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    /**
     * Get all hotels as a real-time stream, ordered by rating.
     */
    fun getHotelsFlow(): Flow<List<Hotel>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_HOTELS)
            .orderBy("rating", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val hotels = snapshot?.toObjects(Hotel::class.java) ?: emptyList()
                trySend(hotels)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Search hotels by city name.
     */
    suspend fun searchHotels(query: String): Result<List<Hotel>> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_HOTELS)
                .orderBy("city")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
            val hotels = snapshot.toObjects(Hotel::class.java)
            Result.success(hotels)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get a single hotel by ID.
     */
    suspend fun getHotelById(hotelId: String): Result<Hotel> {
        return try {
            val snapshot = firestore.collection(Constants.COLLECTION_HOTELS)
                .document(hotelId)
                .get()
                .await()
            val hotel = snapshot.toObject(Hotel::class.java)
                ?: return Result.failure(Exception("Hotel not found"))
            Result.success(hotel)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get rooms for a specific hotel (subcollection).
     */
    fun getRoomsFlow(hotelId: String): Flow<List<Room>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_HOTELS)
            .document(hotelId)
            .collection(Constants.COLLECTION_ROOMS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val rooms = snapshot?.toObjects(Room::class.java) ?: emptyList()
                trySend(rooms)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Add a new hotel (for managers).
     */
    suspend fun addHotel(hotel: Hotel): Result<String> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_HOTELS)
                .add(hotel)
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update an existing hotel.
     */
    suspend fun updateHotel(hotel: Hotel): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_HOTELS)
                .document(hotel.id)
                .set(hotel)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete a hotel.
     */
    suspend fun deleteHotel(hotelId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_HOTELS)
                .document(hotelId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Add a room to a hotel (subcollection).
     */
    suspend fun addRoom(hotelId: String, room: Room): Result<String> {
        return try {
            val docRef = firestore.collection(Constants.COLLECTION_HOTELS)
                .document(hotelId)
                .collection(Constants.COLLECTION_ROOMS)
                .add(room)
                .await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update room availability.
     */
    suspend fun updateRoomAvailability(
        hotelId: String,
        roomId: String,
        isAvailable: Boolean
    ): Result<Unit> {
        return try {
            firestore.collection(Constants.COLLECTION_HOTELS)
                .document(hotelId)
                .collection(Constants.COLLECTION_ROOMS)
                .document(roomId)
                .update("isAvailable", isAvailable)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get hotels managed by a specific manager.
     */
    fun getHotelsByManagerFlow(managerId: String): Flow<List<Hotel>> = callbackFlow {
        val listener = firestore.collection(Constants.COLLECTION_HOTELS)
            .whereEqualTo("managerId", managerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val hotels = snapshot?.toObjects(Hotel::class.java) ?: emptyList()
                trySend(hotels)
            }
        awaitClose { listener.remove() }
    }
}
