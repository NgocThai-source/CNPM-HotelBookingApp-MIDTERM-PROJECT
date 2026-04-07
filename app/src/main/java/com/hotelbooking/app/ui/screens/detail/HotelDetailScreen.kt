package com.hotelbooking.app.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.hotelbooking.app.R
import com.hotelbooking.app.data.model.Room
import com.hotelbooking.app.ui.components.RatingBar
import com.hotelbooking.app.util.UiState
import com.hotelbooking.app.util.toCurrencyString

/**
 * Hotel detail screen showing full information, rooms, and booking options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    onBookClick: (hotelId: String, roomId: String) -> Unit,
    onChatClick: (chatId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: HotelDetailViewModel = hiltViewModel()
) {
    val hotelState by viewModel.hotelState.collectAsState()
    val roomsState by viewModel.roomsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hotel_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Chat with manager */ }) {
                        Icon(Icons.AutoMirrored.Filled.Chat, "Chat")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = hotelState) {
            is UiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
            }

            is UiState.Success -> {
                val hotel = state.data

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Hotel image
                    AsyncImage(
                        model = hotel.imageUrls.firstOrNull(),
                        contentDescription = hotel.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(20.dp)) {
                        // Hotel name & rating
                        Text(
                            text = hotel.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RatingBar(rating = hotel.rating)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${hotel.rating} (${hotel.reviewCount} ${stringResource(R.string.reviews)})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${hotel.address}, ${hotel.city}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Description
                        Text(
                            text = stringResource(R.string.description),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = hotel.description,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Amenities
                        if (hotel.amenities.isNotEmpty()) {
                            Text(
                                text = stringResource(R.string.amenities),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(hotel.amenities) { amenity ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(amenity) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Available rooms
                        Text(
                            text = stringResource(R.string.available_rooms),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        when (val rooms = roomsState) {
                            is UiState.Success -> {
                                rooms.data.forEach { room ->
                                    RoomCard(
                                        room = room,
                                        onBookClick = { onBookClick(hotel.id, room.id) }
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                            is UiState.Loading -> CircularProgressIndicator()
                            else -> {}
                        }
                    }
                }
            }

            is UiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun RoomCard(room: Room, onBookClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = room.imageUrl,
                contentDescription = room.type,
                modifier = Modifier
                    .width(80.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(room.type, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("${room.capacity} khách", style = MaterialTheme.typography.bodySmall)
                Text(
                    "${room.price.toCurrencyString()}/đêm",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Button(
                onClick = onBookClick,
                enabled = room.isAvailable,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (room.isAvailable) "Đặt" else "Hết")
            }
        }
    }
}
