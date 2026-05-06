package com.hotelbooking.app.ui.screens.profile.itemprofilesetting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment


data class Booking(
    val id: Int,
    val check_in_date: String,
    val check_out_date: String,
    val total_price: Double,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryItem(
    navController: NavController,
    bookingList: List<Booking> // 🔥 nhận data từ ngoài
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        if (bookingList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No bookings yet")
            }
        } else {

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                items(bookingList) { booking ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Column(modifier = Modifier.padding(16.dp)) {

                            Text("Booking #${booking.id}")

                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Check-in: ${booking.check_in_date}")
                            Text("Check-out: ${booking.check_out_date}")

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "Total: %,.0f VND".format(booking.total_price)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = booking.status.uppercase(),
                                color = when (booking.status) {
                                    "completed" -> Color.Green
                                    "pending" -> Color(0xFFFFA500)
                                    "cancelled" -> Color.Red
                                    else -> Color.Gray
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
