package com.hotelbooking.app.ui.screens.profile.itemprofilesetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage



data class VietQRTransaction(
    val id: Int,
    val amount: String,
    val qrUrl: String
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentItem(
    navController: NavController
) {

    var transactions by remember {
        mutableStateOf(listOf<VietQRTransaction>())
    }

    var showDialog by remember {
        mutableStateOf(false)
    }

    // ===== THÔNG TIN NGÂN HÀNG =====

    val bankCode = "970422" // MB Bank
    val accountNumber = "0358296442"
    val accountName = "TRAN XUAN THUC"

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Thanh toán VietQR",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {

                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    }
                }
            )
        }

    ) { paddingValues ->


        if (showDialog) {

            AmountInputDialog(

                onDismiss = {
                    showDialog = false
                },

                onConfirm = { amount ->



                    val qrLink =
                        "https://img.vietqr.io/image/" +
                                "$bankCode-$accountNumber-compact2.png" +
                                "?amount=$amount" +
                                "&addInfo=ThanhToanKhachSan" +
                                "&accountName=$accountName"

                    transactions =
                        listOf(
                            VietQRTransaction(
                                id = (0..9999).random(),
                                amount = amount,
                                qrUrl = qrLink
                            )
                        ) + transactions

                    showDialog = false
                }
            )
        }

        // ==============================
        // EMPTY
        // ==============================

        if (transactions.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),

                contentAlignment = Alignment.Center
            ) {

                Text(
                    "Nhấn dấu + để tạo mã QR",
                    color = Color.Gray
                )
            }

        } else {

            // ==============================
            // LIST QR
            // ==============================

            LazyColumn(

                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)

            ) {

                items(transactions) { item ->

                    VietQRCard(item)
                }
            }
        }
    }
}

// ==============================
// DIALOG NHẬP TIỀN
// ==============================

@Composable
fun AmountInputDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    var amount by remember {
        mutableStateOf("")
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Nhập số tiền")
        },

        text = {

            OutlinedTextField(

                value = amount,

                onValueChange = {
                    amount = it
                },

                label = {
                    Text("Số tiền VNĐ")
                },

                modifier = Modifier.fillMaxWidth(),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                singleLine = true
            )
        },

        confirmButton = {

            Button(

                onClick = {

                    if (amount.isNotBlank()) {
                        onConfirm(amount)
                    }
                },

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF005BAC)
                )

            ) {

                Text("Tạo QR")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Hủy")
            }
        }
    )
}

// ==============================
// CARD QR
// ==============================

@Composable
fun VietQRCard(
    transaction: VietQRTransaction
) {

    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),

        shape = RoundedCornerShape(20.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )

    ) {

        Column(

            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Text(
                text = "QUÉT MÃ ĐỂ THANH TOÁN",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Color(0xFF005BAC)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ==============================
            // QR IMAGE
            // ==============================

            AsyncImage(
                model = transaction.qrUrl, // Sử dụng link được tạo động từ transaction
                contentDescription = "VietQR",
                modifier = Modifier
                    .size(240.dp)
                    .background(
                        Color(0xFFF5F5F5),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(10.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Số tiền thanh toán",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Text(
                text = "${transaction.amount} VNĐ",
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(

                color = Color(0xFFEAF4FF),

                shape = RoundedCornerShape(8.dp)

            ) {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                ) {

                    Text(
                        text = "Người nhận: Trần Xuân Thức",
                        fontSize = 12.sp,
                        color = Color(0xFF005BAC),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Ngân hàng: MB Bank",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    Text(
                        text = "STK: 0358296442",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}