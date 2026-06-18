package com.hotelbooking.app.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.hotelbooking.app.data.repository.BookingItem
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.theme.AppColors
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    navController: NavController,
    userId: String,
    isDarkMode: Boolean = false,
    viewModel: MyBookingsViewModel = viewModel()
) {

    val bookings by viewModel.bookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBookings(userId)
    }

    // FIX CURRENT ROUTE
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf(
        "All",
        "Paid",
        "Pending"
    )

    val textColor = AppColors.textPrimary(isDarkMode)
    val subTextColor = AppColors.textSecondary(isDarkMode)
    val bgColor = AppColors.background(isDarkMode)

    val filteredBookings = remember(
        bookings,
        searchQuery,
        selectedFilter
    ) {

        bookings.filter { booking ->

            val matchSearch =
                searchQuery.isBlank() ||
                        booking.hotelTitle.contains(
                            searchQuery,
                            ignoreCase = true
                        ) ||
                        booking.bookingId.contains(
                            searchQuery,
                            ignoreCase = true
                        ) ||
                        booking.guestName.contains(
                            searchQuery,
                            ignoreCase = true
                        )

            val matchFilter = when (selectedFilter) {

                "Paid" -> booking.paymentStatus == "paid"

                "Pending" -> booking.paymentStatus == "pending"

                else -> true
            }

            matchSearch && matchFilter
        }
    }

    Scaffold(
        containerColor = bgColor,

        bottomBar = {

            MyBookingsBottomNav(
                isDarkMode = isDarkMode,
                currentRoute = currentRoute ?: Routes.MY_BOOKINGS
            ) { route ->

                if (route != currentRoute) {

                    navController.navigate(route) {

                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }

    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = {
                viewModel.fetchBookings(userId)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Filled.DateRange,
                            contentDescription = null,
                            tint = AppColors.CyanMain,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Text(
                            text = "My Bookings",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }

                item {

                    OutlinedTextField(
                        value = searchQuery,

                        onValueChange = {
                            searchQuery = it
                        },

                        placeholder = {

                            Text(
                                text = "Search by hotel, guest...",
                                color = subTextColor.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        },

                        leadingIcon = {

                            Icon(
                                Icons.Filled.Search,
                                contentDescription = null,
                                tint = subTextColor
                            )
                        },

                        modifier = Modifier.fillMaxWidth(),

                        shape = RoundedCornerShape(14.dp),

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.CyanMain,
                            unfocusedBorderColor = AppColors.border(isDarkMode),
                            focusedContainerColor = AppColors.surface(isDarkMode),
                            unfocusedContainerColor = AppColors.surface(isDarkMode)
                        ),

                        singleLine = true
                    )
                }

                item {

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(filters) { filter ->

                            val isSelected =
                                filter == selectedFilter

                            Surface(
                                onClick = {
                                    selectedFilter = filter
                                },

                                shape = RoundedCornerShape(20.dp),

                                color =
                                    if (isSelected)
                                        AppColors.CyanMain
                                    else
                                        AppColors.surface(isDarkMode),

                                shadowElevation = 1.dp
                            ) {

                                Text(
                                    text = filter,

                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),

                                    fontSize = 13.sp,

                                    fontWeight = FontWeight.SemiBold,

                                    color =
                                        if (isSelected)
                                            Color.White
                                        else
                                            subTextColor
                                )
                            }
                        }
                    }
                }

                items(
                    filteredBookings,
                    key = { it.bookingId }
                ) { booking ->

                    BookingListCard(
                        booking = booking,
                        isDarkMode = isDarkMode,

                        onPaymentRetry =
                            if (booking.paymentStatus == "pending") {

                                { b ->

                                    val encodedTitle =
                                        URLEncoder.encode(
                                            b.hotelTitle,
                                            "UTF-8"
                                        )

                                    val encodedImage =
                                        URLEncoder.encode(
                                            b.hotelImageUrl,
                                            "UTF-8"
                                        )

                                    val paymentRoute =
                                        "payment/${b.bookingId}/$encodedTitle/$encodedImage/${b.guestName}/${b.phone}/${b.totalPrice}/${(b.totalPrice * 25000).toLong()}/${b.checkInDate}/${b.checkOutDate}/${b.numberOfNights}"

                                    navController.navigate(paymentRoute)
                                }

                            } else null
                    )
                }

                if (filteredBookings.isEmpty() && !isLoading) {

                    item {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp),

                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "No bookings found",
                                color = subTextColor
                            )
                        }
                    }
                }

                item {

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BookingListCard(
    booking: BookingItem,
    isDarkMode: Boolean,
    onPaymentRetry: ((BookingItem) -> Unit)? = null
) {

    val textColor = AppColors.textPrimary(isDarkMode)
    val subTextColor = AppColors.textSecondary(isDarkMode)

    val currencyFormatter = remember {
        NumberFormat.getNumberInstance(Locale.US)
    }

    val isPaid = booking.paymentStatus == "paid"
    val isPending = booking.paymentStatus == "pending"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isPending && onPaymentRetry != null)
                    Modifier.clickable {
                        onPaymentRetry(booking)
                    }
                else
                    Modifier
            ),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(
            containerColor = AppColors.surface(isDarkMode)
        ),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                AsyncImage(
                    model = booking.hotelImageUrl,
                    contentDescription = booking.hotelTitle,
                    contentScale = ContentScale.Crop,

                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = booking.hotelTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "ID: ${booking.bookingId}",
                        fontSize = 12.sp,
                        color = AppColors.CyanMain,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Guest: ${booking.guestName}",
                        fontSize = 12.sp,
                        color = subTextColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),

                    color =
                        if (isPaid)
                            Color(0xFFE8F5E9)
                        else
                            Color(0xFFFFF3E0)
                ) {

                    Text(
                        text =
                            if (isPaid)
                                "Paid"
                            else
                                "Pending",

                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),

                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,

                        color =
                            if (isPaid)
                                Color(0xFF2E7D32)
                            else
                                Color(0xFFE65100)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                thickness = 0.5.dp,
                color = AppColors
                    .border(isDarkMode)
                    .copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.Bottom
            ) {

                Column {

                    Text(
                        text = "${booking.checkInDate} - ${booking.checkOutDate}",
                        fontSize = 12.sp,
                        color = subTextColor
                    )

                    Text(
                        text = "${booking.numberOfNights} nights",
                        fontSize = 11.sp,
                        color = subTextColor.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = "$${currencyFormatter.format(booking.totalPrice)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.CyanMain
                )
            }

            if (isPending) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            AppColors.CyanMain.copy(alpha = 0.1f)
                        )
                        .padding(vertical = 8.dp),

                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Tap to complete payment",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.CyanMain
                        )

                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = AppColors.CyanMain,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyBookingsBottomNav(
    isDarkMode: Boolean,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {

    NavigationBar(
        containerColor = AppColors.surface(isDarkMode),
        tonalElevation = 8.dp
    ) {

        // HOME
        NavigationBarItem(

            selected = currentRoute == Routes.HOME,

            onClick = {
                onNavigate(Routes.HOME)
            },

            alwaysShowLabel = true,

            icon = {

                Icon(
                    imageVector =
                        if (currentRoute == Routes.HOME)
                            Icons.Filled.Home
                        else
                            Icons.Outlined.Home,

                    contentDescription = "Home",

                    tint =
                        if (currentRoute == Routes.HOME)
                            AppColors.CyanMain
                        else
                            AppColors.textTertiary(isDarkMode)
                )
            },

            label = {

                Text(
                    text = "Home",
                    fontSize = 11.sp,

                    color =
                        if (currentRoute == Routes.HOME)
                            AppColors.CyanMain
                        else
                            AppColors.textTertiary(isDarkMode)
                )
            },

            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        // BOOKINGS
        NavigationBarItem(

            selected = currentRoute == Routes.MY_BOOKINGS,

            onClick = {
                onNavigate(Routes.MY_BOOKINGS)
            },

            alwaysShowLabel = true,

            icon = {

                Icon(
                    imageVector =
                        if (currentRoute == Routes.MY_BOOKINGS)
                            Icons.Filled.Book
                        else
                            Icons.Outlined.Book,

                    contentDescription = "Bookings",

                    tint =
                        if (currentRoute == Routes.MY_BOOKINGS)
                            AppColors.CyanMain
                        else
                            AppColors.textTertiary(isDarkMode)
                )
            },

            label = {

                Text(
                    text = "Bookings",
                    fontSize = 11.sp,

                    color =
                        if (currentRoute == Routes.MY_BOOKINGS)
                            AppColors.CyanMain
                        else
                            AppColors.textTertiary(isDarkMode)
                )
            },

            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        // NOTIFICATIONS
        NavigationBarItem(

            selected = currentRoute == Routes.NOTIFICATIONS,

            onClick = {
                onNavigate(Routes.NOTIFICATIONS)
            },

            alwaysShowLabel = true,

            icon = {

                Icon(
                    imageVector =
                        if (currentRoute == Routes.NOTIFICATIONS)
                            Icons.Filled.Notifications
                        else
                            Icons.Outlined.Notifications,

                    contentDescription = "Notifications",

                    tint =
                        if (currentRoute == Routes.NOTIFICATIONS)
                            AppColors.CyanMain
                        else
                            AppColors.textTertiary(isDarkMode)
                )
            },

            label = {

                Text(
                    text = "Notify",
                    fontSize = 11.sp,

                    color =
                        if (currentRoute == Routes.NOTIFICATIONS)
                            AppColors.CyanMain
                        else
                            AppColors.textTertiary(isDarkMode)
                )
            },

            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        // SETTINGS
        NavigationBarItem(

            selected = currentRoute == Routes.PROFILE_SETTING,

            onClick = {
                onNavigate(Routes.PROFILE_SETTING)
            },

            alwaysShowLabel = true,

            icon = {

                Icon(
                    imageVector =
                        if (currentRoute == Routes.PROFILE_SETTING)
                            Icons.Filled.Settings
                        else
                            Icons.Outlined.Settings,

                    contentDescription = "Settings",

                    tint =
                        if (currentRoute == Routes.PROFILE_SETTING)
                            AppColors.CyanMain
                        else
                            AppColors.textTertiary(isDarkMode)
                )
            },

            label = {

                Text(
                    text = "Settings",
                    fontSize = 11.sp,

                    color =
                        if (currentRoute == Routes.PROFILE_SETTING)
                            AppColors.CyanMain
                        else
                            AppColors.textTertiary(isDarkMode)
                )
            },

            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
    }
}