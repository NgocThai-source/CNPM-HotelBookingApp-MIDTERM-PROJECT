package com.hotelbooking.app.ui.screens.payment

import android.widget.Toast
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    bookingId: String,
    hotelName: String,
    hotelImageUrl: String,
    guestName: String,
    phone: String,
    totalPriceUSD: Double,
    totalPriceVND: Long,
    checkInDate: String,
    checkOutDate: String,
    numberOfNights: Int,
    viewModel: PaymentViewModel = viewModel()
) {

    val context = LocalContext.current

    val state by viewModel.state.collectAsState()
    val countdown by viewModel.countdownSeconds.collectAsState()

    val isDarkMode = false

    val textColor = AppColors.textPrimary(isDarkMode)
    val subTextColor = AppColors.textSecondary(isDarkMode)
    val bgColor = AppColors.background(isDarkMode)

    LaunchedEffect(Unit) {
        viewModel.initState(
            bookingId = bookingId,
            hotelName = hotelName,
            hotelImageUrl = hotelImageUrl,
            guestName = guestName,
            phone = phone,
            totalPriceUSD = totalPriceUSD,
            totalPriceVND = totalPriceVND,
            checkInDate = checkInDate,
            checkOutDate = checkOutDate,
            numberOfNights = numberOfNights
        )
    }

    LaunchedEffect(state.paidSuccess) {
        if (state.paidSuccess) {

            var remaining = 5

            while (remaining > 0) {
                viewModel.decrementCountdown()
                delay(1000)
                remaining--
            }

            navController.navigate(Routes.MY_BOOKINGS) {
                popUpTo(Routes.HOME) {
                    inclusive = false
                }
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {

            Toast.makeText(
                context,
                it,
                Toast.LENGTH_SHORT
            ).show()

            viewModel.clearError()
        }
    }

    /*
    =========================
          VIETQR INFO
    =========================
    */

    val bankName = "MBBank"

    val bankId = "970422"

    val accountNumber = "0358296442"

    val accountName = "TRAN XUAN THUC"


    val currencyFormatter = remember {
        NumberFormat.getNumberInstance(Locale.US)
    }

    val vndFormatter = remember {
        NumberFormat.getNumberInstance(Locale("vi", "VN"))
    }

    val vietQrUrl =
        "https://img.vietqr.io/image/" +
                "$bankId-$accountNumber-compact2.png" +
                "?amount=$totalPriceVND" +
                "&addInfo=$bookingId" +
                "&accountName=${
                    URLEncoder.encode(
                        accountName,
                        "UTF-8"
                    )
                }"

    val animatedCountdown by animateIntAsState(
        targetValue = countdown,
        label = "countdown"
    )

    Scaffold(
        containerColor = bgColor
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            /*
            =========================
                  HEADER
            =========================
            */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickableNoRipple {
                            navController.popBackStack()
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "Payment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                /*
                =========================
                   HOTEL INFORMATION
                =========================
                */

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AsyncImage(
                        model = hotelImageUrl,
                        contentDescription = hotelName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = hotelName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Booking ID: $bookingId",
                            fontSize = 12.sp,
                            color = AppColors.CyanMain,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                /*
                =========================
                     QR PAYMENT
                =========================
                */

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Scan QR to pay",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(
                                    2.dp,
                                    AppColors.CyanMain.copy(alpha = 0.3f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            AsyncImage(
                                model = vietQrUrl,
                                contentDescription = "VietQR",
                                modifier = Modifier
                                    .size(196.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        VietQRInfoRow(
                            label = "Bank",
                            value = bankName,
                            textColor = textColor,
                            subTextColor = subTextColor
                        )

                        VietQRInfoRow(
                            label = "Account No.",
                            value = accountNumber,
                            textColor = textColor,
                            subTextColor = subTextColor
                        )

                        VietQRInfoRow(
                            label = "Account Name",
                            value = accountName,
                            textColor = textColor,
                            subTextColor = subTextColor
                        )

                        VietQRInfoRow(
                            label = "Amount",
                            value = "${vndFormatter.format(totalPriceVND)} VND",
                            textColor = AppColors.CyanMain,
                            subTextColor = subTextColor,
                            isHighlight = true
                        )

                        VietQRInfoRow(
                            label = "Reference",
                            value = bookingId,
                            textColor = textColor,
                            subTextColor = subTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                /*
                =========================
                    BOOKING DETAILS
                =========================
                */

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.surface(isDarkMode),
                    shadowElevation = 2.dp
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "Booking Details",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PaymentInfoRow(
                            "Guest",
                            guestName,
                            textColor,
                            subTextColor
                        )

                        PaymentInfoRow(
                            "Phone",
                            phone,
                            textColor,
                            subTextColor
                        )

                        PaymentInfoRow(
                            "Check-in",
                            checkInDate,
                            textColor,
                            subTextColor
                        )

                        PaymentInfoRow(
                            "Check-out",
                            checkOutDate,
                            textColor,
                            subTextColor
                        )

                        PaymentInfoRow(
                            "Nights",
                            "$numberOfNights night${if (numberOfNights > 1) "s" else ""}",
                            textColor,
                            subTextColor
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = AppColors.border(isDarkMode)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                "Total",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            Text(
                                "${vndFormatter.format(totalPriceVND)} VND",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppColors.CyanMain
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                /*
                =========================
                    PAYMENT SUCCESS
                =========================
                */

                if (state.paidSuccess) {

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFE8F5E9)
                    ) {

                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Payment confirmed! Redirecting in $animatedCountdown sec...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                /*
                =========================
                    ACTION BUTTONS
                =========================
                */

                if (!state.paidSuccess) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        OutlinedButton(
                            onClick = {
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = textColor
                            )
                        ) {

                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Back",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.markAsPaid()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            enabled = !state.isMarkingPaid
                        ) {

                            if (state.isMarkingPaid) {

                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )

                            } else {

                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color.White
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = "Payment Done",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                } else {

                    Button(
                        onClick = {
                            navController.navigate(Routes.MY_BOOKINGS) {
                                popUpTo(Routes.HOME) {
                                    inclusive = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.CyanMain
                        )
                    ) {

                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Go to My Bookings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun VietQRInfoRow(
    label: String,
    value: String,
    textColor: Color,
    subTextColor: Color,
    isHighlight: Boolean = false
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            fontSize = 13.sp,
            color = subTextColor
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (isHighlight)
                FontWeight.ExtraBold
            else
                FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
private fun PaymentInfoRow(
    label: String,
    value: String,
    textColor: Color,
    subTextColor: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            fontSize = 13.sp,
            color = subTextColor
        )

        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

private fun Modifier.clickableNoRipple(
    onClick: () -> Unit
): Modifier = composed {

    clickable(
        indication = null,
        interactionSource = remember {
            MutableInteractionSource()
        },
        onClick = onClick
    )
}
