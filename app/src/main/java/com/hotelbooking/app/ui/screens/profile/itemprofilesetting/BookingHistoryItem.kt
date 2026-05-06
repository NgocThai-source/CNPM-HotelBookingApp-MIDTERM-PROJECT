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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryItem(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking History") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Dùng popBackStack an toàn hơn
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Thêm Modifier.fillMaxSize() để LazyColumn chiếm hết màn hình
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Hiển thị 5 phần tử mẫu
            items(5) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Booking #$index",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}