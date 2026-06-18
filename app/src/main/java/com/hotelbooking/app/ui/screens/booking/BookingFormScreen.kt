package com.hotelbooking.app.ui.screens.booking

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    userId: String,
    navController: NavController,
    hotelId: String,
    hotelTitle: String,
    hotelPrice: Double,
    hotelImageUrl: String,
    checkInAvailable: String,
    checkOutAvailable: String,
    exchangeRate: Double,
    viewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initWithHotelData(
            hotelId = hotelId,
            hotelTitle = hotelTitle,
            hotelPrice = hotelPrice,
            hotelImageUrl = hotelImageUrl,
            checkInAvailable = checkInAvailable,
            checkOutAvailable = checkOutAvailable,
            exchangeRate = exchangeRate
        )
    }

    LaunchedEffect(formState.submitSuccess) {
        if (formState.submitSuccess) {
            Toast.makeText(context, "Booking confirmed! ID: ${formState.bookingId ?: "N/A"}", Toast.LENGTH_LONG).show()
            val dateFormatDisplay = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val checkInFormatted = formState.checkInDate?.let { dateFormatDisplay.format(Date(it)) } ?: ""
            val checkOutFormatted = formState.checkOutDate?.let { dateFormatDisplay.format(Date(it)) } ?: ""
            val route = paymentRoute(
                bookingId = formState.bookingId ?: "BK-XXXXX",
                hotelName = formState.hotelTitle,
                hotelImageUrl = formState.hotelImageUrl,
                guestName = formState.guestName,
                phone = formState.phone,
                totalPriceUSD = viewModel.totalPriceUSD,
                totalPriceVND = viewModel.totalPriceVND,
                checkInDate = checkInFormatted,
                checkOutDate = checkOutFormatted,
                numberOfNights = viewModel.numberOfNights
            )
            navController.navigate(route)
        }
    }

    LaunchedEffect(formState.submitError) {
        formState.submitError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    var showCheckInPicker by remember { mutableStateOf(false) }
    var showCheckOutPicker by remember { mutableStateOf(false) }

    val minCheckIn = remember { parseDate(checkInAvailable.ifEmpty { "2026-01-01" }) }
    val maxCheckIn = remember { parseDate(checkOutAvailable.ifEmpty { "2030-12-31" }) }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.US) }
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.US) }
    val vndFormatter = remember { NumberFormat.getNumberInstance(Locale("vi", "VN")) }

    val textColor = AppColors.textPrimary(false)
    val subTextColor = AppColors.textSecondary(false)
    val bgColor = AppColors.background(false)

    Scaffold(
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero image header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = hotelImageUrl,
                    contentDescription = hotelTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(colors = listOf(Color.Transparent, bgColor)))
                )

                // Back button
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable { navController.popBackStack() }
                        .align(Alignment.TopStart),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = textColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Hotel info
                Text(
                    text = hotelTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${currencyFormatter.format(hotelPrice)} / night",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.CyanMain
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Guest Information
                Text(
                    text = "Guest Information",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Full Name
                FormTextField(
                    label = "Full Name",
                    value = formState.guestName,
                    onValueChange = { viewModel.updateGuestName(it) },
                    placeholder = "Enter your full name",
                    leadingIcon = Icons.Default.Person,
                    textColor = textColor,
                    subTextColor = subTextColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Phone Number
                FormTextField(
                    label = "Phone Number",
                    value = formState.phone,
                    onValueChange = { viewModel.updatePhone(it) },
                    placeholder = "Enter your phone number",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    textColor = textColor,
                    subTextColor = subTextColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Stay Details
                Text(
                    text = "Stay Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Check-in date
                DateSelector(
                    label = "Check-in Date",
                    value = formState.checkInDate?.let { dateFormatter.format(Date(it)) } ?: "Select check-in date",
                    onClick = { showCheckInPicker = true },
                    textColor = textColor,
                    subTextColor = subTextColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Check-out date
                DateSelector(
                    label = "Check-out Date",
                    value = formState.checkOutDate?.let { dateFormatter.format(Date(it)) } ?: "Select check-out date",
                    onClick = { showCheckOutPicker = true },
                    textColor = textColor,
                    subTextColor = subTextColor,
                    enabled = formState.checkInDate != null
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Guest count
                GuestCountSelector(
                    count = formState.guestCount,
                    onIncrease = { viewModel.updateGuestCount(formState.guestCount + 1) },
                    onDecrease = { viewModel.updateGuestCount(formState.guestCount - 1) },
                    textColor = textColor,
                    subTextColor = subTextColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Price Preview
                Text(
                    text = "Price Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = AppColors.surface(false),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        PriceRow(label = "Price per night", value = "$${currencyFormatter.format(hotelPrice)}", textColor = subTextColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        PriceRow(label = "Number of nights", value = "${viewModel.numberOfNights}", textColor = subTextColor)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = AppColors.border(false))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total (USD)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor)
                            Text(
                                "$${currencyFormatter.format(viewModel.totalPriceUSD)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppColors.CyanMain
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total (VND)", fontSize = 13.sp, color = subTextColor)
                            Text(
                                "${vndFormatter.format(viewModel.totalPriceVND)} VND",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = subTextColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Confirm Booking Button
                Button(
                    onClick = { viewModel.submitBooking(userId) }, // <--- TRUYỀN USER ID TẠI ĐÂY LÀ ĐÚNG CHUẨN
                    enabled = viewModel.isFormValid && !formState.isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.CyanMain,
                        disabledContainerColor = AppColors.CyanMain.copy(alpha = 0.4f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    if (formState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Confirm Booking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Check-in date picker dialog
    if (showCheckInPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.checkInDate ?: minCheckIn,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= minCheckIn && utcTimeMillis <= maxCheckIn
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showCheckInPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.updateCheckInDate(it) }
                    showCheckInPicker = false
                }) {
                    Text("Confirm", color = AppColors.CyanMain)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckInPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = AppColors.CyanMain,
                    todayContentColor = AppColors.CyanMain,
                    todayDateBorderColor = AppColors.CyanMain
                )
            )
        }
    }

    // Check-out date picker dialog
    if (showCheckOutPicker) {
        val minCheckOut = (formState.checkInDate ?: minCheckIn) + TimeUnit.DAYS.toMillis(1)
        val maxCheckOut = maxCheckIn

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.checkOutDate ?: minCheckOut,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= minCheckOut && utcTimeMillis <= maxCheckOut
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { showCheckOutPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.updateCheckOutDate(it) }
                    showCheckOutPicker = false
                }) {
                    Text("Confirm", color = AppColors.CyanMain)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckOutPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = AppColors.CyanMain,
                    todayContentColor = AppColors.CyanMain,
                    todayDateBorderColor = AppColors.CyanMain
                )
            )
        }
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    textColor: Color,
    subTextColor: Color
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = subTextColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = subTextColor.copy(alpha = 0.5f), fontSize = 14.sp) },
            leadingIcon = {
                Icon(leadingIcon, contentDescription = null, tint = AppColors.CyanMain, modifier = Modifier.size(20.dp))
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.CyanMain,
                unfocusedBorderColor = AppColors.border(false),
                focusedContainerColor = AppColors.surface(false),
                unfocusedContainerColor = AppColors.surface(false),
                cursorColor = AppColors.CyanMain
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = textColor, fontSize = 15.sp)
        )
    }
}

@Composable
private fun DateSelector(
    label: String,
    value: String,
    onClick: () -> Unit,
    textColor: Color,
    subTextColor: Color,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = subTextColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Surface(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = AppColors.surface(false),
            shadowElevation = if (enabled) 1.dp else 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = AppColors.CyanMain,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = value,
                        fontSize = 15.sp,
                        color = if (enabled) textColor else subTextColor.copy(alpha = 0.5f)
                    )
                }
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = subTextColor.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        if (!enabled) {
            Text(
                text = "Please select check-in date first",
                fontSize = 11.sp,
                color = subTextColor.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun GuestCountSelector(
    count: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    textColor: Color,
    subTextColor: Color
) {
    Column {
        Text(
            text = "Number of Guests",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = subTextColor,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = AppColors.surface(false),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = AppColors.CyanMain,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Guests", fontSize = 15.sp, color = textColor)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onDecrease,
                        enabled = count > 1,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (count > 1) AppColors.CyanMain.copy(alpha = 0.15f)
                                else AppColors.border(false).copy(alpha = 0.3f)
                            )
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease",
                            tint = if (count > 1) AppColors.CyanMain else subTextColor.copy(alpha = 0.4f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "$count",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.widthIn(min = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AppColors.CyanMain.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = AppColors.CyanMain,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = textColor)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

private fun parseDate(dateStr: String): Long {
    return try {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

private fun paymentRoute(
    bookingId: String,
    hotelName: String,
    hotelImageUrl: String,
    guestName: String,
    phone: String,
    totalPriceUSD: Double,
    totalPriceVND: Long,
    checkInDate: String,
    checkOutDate: String,
    numberOfNights: Int
): String {
    val encodedName = URLEncoder.encode(hotelName, "UTF-8")
    val encodedImg = URLEncoder.encode(hotelImageUrl, "UTF-8")
    val encodedGuest = URLEncoder.encode(guestName, "UTF-8")
    return "payment/$bookingId/$encodedName/$encodedImg/$encodedGuest/$phone/$totalPriceUSD/$totalPriceVND/${URLEncoder.encode(checkInDate, "UTF-8")}/${URLEncoder.encode(checkOutDate, "UTF-8")}/$numberOfNights"
}