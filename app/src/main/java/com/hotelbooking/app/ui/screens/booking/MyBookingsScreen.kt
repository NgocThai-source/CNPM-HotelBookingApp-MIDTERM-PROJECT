package com.hotelbooking.app.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.hotelbooking.app.data.model.Booking
import com.hotelbooking.app.data.model.BookingStatus
import com.hotelbooking.app.ui.theme.*
import com.hotelbooking.app.util.DateUtils.toDisplayString
import com.hotelbooking.app.util.UiState
import com.hotelbooking.app.util.toCurrencyString

/**
 * My Bookings screen showing user's booking history.
 */
@Composable
fun MyBookingsScreen(
    onBookingClick: (String) -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val bookingsState by viewModel.userBookings.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.my_bookings),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = bookingsState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.no_bookings),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data, key = { it.id }) { booking ->
                            BookingCard(
                                booking = booking,
                                onCancel = { viewModel.cancelBooking(booking.id) }
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun BookingCard(booking: Booking, onCancel: () -> Unit) {
    val statusColor = when (booking.status) {
        BookingStatus.PENDING -> StatusPending
        BookingStatus.CONFIRMED -> StatusConfirmed
        BookingStatus.CANCELLED -> StatusCancelled
        BookingStatus.COMPLETED -> StatusCompleted
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = booking.hotelImageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    booking.hotelName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(booking.roomType, style = MaterialTheme.typography.bodySmall)

                val checkIn = booking.checkIn?.toDisplayString() ?: "—"
                val checkOut = booking.checkOut?.toDisplayString() ?: "—"
                Text(
                    "$checkIn → $checkOut",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                booking.status.name,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = statusColor.copy(alpha = 0.15f),
                            labelColor = statusColor
                        )
                    )
                    Text(
                        booking.totalPrice.toCurrencyString(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (booking.status == BookingStatus.PENDING) {
                    TextButton(onClick = onCancel) {
                        Text(
                            stringResource(R.string.cancel_booking),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
