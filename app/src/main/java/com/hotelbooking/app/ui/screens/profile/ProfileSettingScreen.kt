package com.hotelbooking.app.ui.screens.profile

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hotelbooking.app.R
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.ui.theme.AppColors
import com.hotelbooking.app.util.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

// ─────────────────────────────────────────────────────────────
// ProfileSettingScreen
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingScreen(
    viewModel: ProfileSettingViewModel,
    isDarkMode: Boolean,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val favoritesState by FavoritesViewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showChangePasswordSheet by remember { mutableStateOf(false) }
    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }

    // Preferences state (UI-only / placeholder)
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(isDarkMode) }
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedCurrency by remember { mutableStateOf("USD") }

    // Reduced motion
    val preferReducedMotion = remember(context) {
        android.provider.Settings.Global.getFloat(
            context.contentResolver,
            android.provider.Settings.Global.ANIMATOR_DURATION_SCALE,
            1.0f
        ) == 0.0f
    }

    LaunchedEffect(state.profile?.userId) {
        state.profile?.let { editedName = it.fullName }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(state.passwordChangeSuccess) {
        if (state.passwordChangeSuccess) {
            Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()
            showChangePasswordSheet = false
            viewModel.clearPasswordState()
        }
    }

    LaunchedEffect(state.passwordChangeError) {
        state.passwordChangeError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearPasswordState()
        }
    }

    LaunchedEffect(favoritesState.actionMessage) {
        favoritesState.actionMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            FavoritesViewModel.clearActionMessage()
        }
    }

    val screenBackground = if (isDarkMode) {
        Brush.verticalGradient(
            listOf(AppColors.DarkBackground, Color(0xFF0D1520), AppColors.DarkBackground)
        )
    } else {
        Brush.verticalGradient(
            listOf(AppColors.CreamLight, AppColors.CreamSurface, Color(0xFFF2EDE5))
        )
    }

    Scaffold(
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackground)
        ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── GRADIENT PROFILE HEADER ──────────────────────────────
            item {
                ProfileGradientHeader(
                    name = state.profile?.fullName ?: "",
                    email = state.profile?.email ?: "",
                    isLoading = state.isLoading && state.profile == null,
                    isDarkMode = isDarkMode,
                    onBack = onBack,
                    bookingCount = 12,
                    savedCount = favoritesState.favorites.size,
                    points = 850,
                    preferReducedMotion = preferReducedMotion
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            // ── PREFERENCES ────────────────────────────────────────
            item {
                PreferencesSectionCard(
                    notificationsEnabled = notificationsEnabled,
                    onNotificationsToggle = { notificationsEnabled = it },
                    darkModeEnabled = darkModeEnabled,
                    onDarkModeToggle = { darkModeEnabled = it },
                    selectedLanguage = selectedLanguage,
                    selectedCurrency = selectedCurrency,
                    isDarkMode = isDarkMode,
                    preferReducedMotion = preferReducedMotion
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            // ── ACCOUNT INFORMATION ────────────────────────────────
            item {
                PremiumSectionHeader(
                    title = "Account",
                    icon = Icons.Filled.Person,
                    isDarkMode = isDarkMode
                )
            }

            item {
                PremiumSectionCard(isDarkMode = isDarkMode, preferReducedMotion = preferReducedMotion) {
                    if (state.isLoading && state.profile == null) {
                        PremiumInfoRowSkeleton(isDarkMode = isDarkMode)
                        PremiumDivider(isDarkMode)
                        PremiumInfoRowSkeleton(isDarkMode = isDarkMode)
                        PremiumDivider(isDarkMode)
                        PremiumInfoRowSkeleton(isDarkMode = isDarkMode)
                    } else {
                        PremiumInfoRow(
                            icon = Icons.Filled.Email,
                            label = "Email",
                            value = state.profile?.email ?: "—",
                            iconTint = AppColors.SkyBrand,
                            iconBg = AppColors.SkyBrand.copy(alpha = 0.1f),
                            isDarkMode = isDarkMode
                        )
                        PremiumDivider(isDarkMode)
                        PremiumInfoRow(
                            icon = Icons.Filled.Phone,
                            label = "Phone",
                            value = state.profile?.phone?.ifEmpty { "Not set" } ?: "Not set",
                            iconTint = AppColors.SkyBrand,
                            iconBg = AppColors.SkyBrand.copy(alpha = 0.1f),
                            isDarkMode = isDarkMode
                        )
                        PremiumDivider(isDarkMode)
                        PremiumEditableNameRow(
                            value = editedName,
                            isEditing = isEditingName,
                            isLoading = state.isLoading,
                            onEditClick = {
                                editedName = state.profile?.fullName ?: ""
                                isEditingName = true
                            },
                            onSave = {
                                scope.launch { viewModel.updateName(editedName) }
                                isEditingName = false
                            },
                            onCancel = {
                                editedName = state.profile?.fullName ?: ""
                                isEditingName = false
                            },
                            onValueChange = { editedName = it },
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // ── SECURITY ──────────────────────────────────────────
            item {
                PremiumSectionHeader(
                    title = "Security",
                    icon = Icons.Filled.Lock,
                    isDarkMode = isDarkMode
                )
            }

            item {
                PremiumSectionCard(isDarkMode = isDarkMode, preferReducedMotion = preferReducedMotion) {
                    PremiumSettingsRow(
                        icon = Icons.Filled.Lock,
                        title = "Change Password",
                        subtitle = "Update your account password",
                        iconTint = AppColors.GoldPrimary,
                        iconBg = AppColors.GoldPrimary.copy(alpha = 0.1f),
                        onClick = { showChangePasswordSheet = true },
                        isDarkMode = isDarkMode,
                        preferReducedMotion = preferReducedMotion
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // ── SAVED HOTELS ──────────────────────────────────────
            item {
                PremiumSectionHeader(
                    title = "Saved Hotels",
                    icon = Icons.Filled.FavoriteBorder,
                    isDarkMode = isDarkMode
                )
            }

            when {
                favoritesState.isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = AppColors.SkyBrand,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                favoritesState.favorites.isEmpty() -> {
                    item {
                        PremiumSectionCard(isDarkMode = isDarkMode, preferReducedMotion = preferReducedMotion) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(AppColors.Error.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.FavoriteBorder,
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        tint = AppColors.Error.copy(alpha = 0.5f)
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "No saved hotels yet",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppColors.textSecondary(isDarkMode)
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    "Tap heart on hotels to save them here",
                                    fontSize = 12.sp,
                                    color = AppColors.textTertiary(isDarkMode)
                                )
                            }
                        }
                    }
                }

                else -> {
                    items(favoritesState.favorites) { hotel ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                            FavoriteHotelCard(
                                hotel = hotel,
                                isDarkMode = isDarkMode,
                                preferReducedMotion = preferReducedMotion
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }

            // ── SUPPORT & ABOUT ───────────────────────────────────
            item {
                SupportSectionCard(isDarkMode = isDarkMode, preferReducedMotion = preferReducedMotion)
            }

            item { Spacer(Modifier.height(24.dp)) }

            // ── SIGN OUT ─────────────────────────────────────────
            item {
                SignOutButton(
                    onSignOut = {
                        TokenManager.clearToken()
                        Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                        onLogout()
                    },
                    isDarkMode = isDarkMode,
                    preferReducedMotion = preferReducedMotion
                )
            }
        }
        } // Box gradient background
    }

    // ── Change Password Bottom Sheet ──────────────────────────────
    if (showChangePasswordSheet) {
        ChangePasswordBottomSheet(
            onDismiss = {
                showChangePasswordSheet = false
                viewModel.clearPasswordState()
            },
            onConfirm = { oldPass, newPass ->
                viewModel.changePassword(oldPass, newPass)
            },
            isLoading = state.isLoading,
            isDarkMode = isDarkMode
        )
    }
}

// ─────────────────────────────────────────────────────────────
// GRADIENT PROFILE HEADER
// ─────────────────────────────────────────────────────────────
@Composable
private fun ProfileGradientHeader(
    name: String,
    email: String,
    isLoading: Boolean,
    isDarkMode: Boolean,
    onBack: () -> Unit,
    bookingCount: Int,
    savedCount: Int,
    points: Int,
    preferReducedMotion: Boolean
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = if (preferReducedMotion) tween(0) else tween(500),
        label = "header_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .graphicsLayer { this.alpha = alpha }
    ) {
        // Navy gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .background(
                    Brush.verticalGradient(listOf(AppColors.NavyDeep, AppColors.NavyMid))
                )
        )

        // Decorative gold glow bottom-left
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = 30.dp)
                .background(
                    Brush.radialGradient(
                        listOf(AppColors.GoldPrimary.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )

        // Decorative sky glow top-right
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .background(
                    Brush.radialGradient(
                        listOf(AppColors.SkyBrand.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back button row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = "Settings",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            // Avatar with gradient ring + camera overlay
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(AppColors.SkyBrand, AppColors.GoldPrimary.copy(alpha = 0.8f))
                            )
                        )
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(AppColors.NavyMid),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = AppColors.SkyBrand,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        } else {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.size(68.dp),
                                tint = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        // Gradient overlay at bottom of avatar for text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.15f))
                                    ),
                                    shape = RoundedCornerShape(bottomStart = 99.dp, bottomEnd = 99.dp)
                                )
                        )
                    }
                }

                // Camera overlay button (bottom-end of avatar)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(AppColors.SkyBrand, AppColors.SkyDark)
                            )
                        )
                        .then(
                            Modifier.shadow(
                                elevation = 4.dp,
                                shape = CircleShape,
                                ambientColor = AppColors.SkyBrand.copy(alpha = 0.3f),
                                spotColor = AppColors.SkyBrand.copy(alpha = 0.4f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.CameraAlt,
                        contentDescription = "Change photo",
                        modifier = Modifier.size(15.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            if (!isLoading) {
                Text(
                    text = name.ifEmpty { "Set your name" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(3.dp))
                if (email.isNotEmpty()) {
                    Text(
                        text = email,
                        fontSize = 12.sp,
                        color = AppColors.SkyBrand.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                )
                Spacer(Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                )
            }

            Spacer(Modifier.height(10.dp))

            // Premium badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                AppColors.GoldPrimary.copy(alpha = 0.25f),
                                AppColors.GoldDark.copy(alpha = 0.15f)
                            )
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(Icons.Filled.Stars, null, Modifier.size(13.dp), AppColors.GoldPrimary)
                    Text(
                        "Premium Member",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.GoldPrimary
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Stats bar (glass card)
            StatsBar(
                bookingCount = bookingCount,
                savedCount = savedCount,
                points = points,
                isDarkMode = isDarkMode
            )

            Spacer(Modifier.height(8.dp))
        }

        // Bottom fade — blends into cream background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            if (isDarkMode) AppColors.DarkBackground else AppColors.CreamSurface
                        )
                    )
                )
        )
    }
}

// ─────────────────────────────────────────────────────────────
// STATS BAR
// ─────────────────────────────────────────────────────────────
@Composable
private fun StatsBar(
    bookingCount: Int,
    savedCount: Int,
    points: Int,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AppColors.GlassWhite)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            icon = Icons.Filled.DateRange,
            value = bookingCount.toString(),
            label = "Bookings",
            isDarkMode = isDarkMode
        )
        StatDivider(isDarkMode)
        StatItem(
            icon = Icons.Filled.Favorite,
            value = savedCount.toString(),
            label = "Saved",
            isDarkMode = isDarkMode
        )
        StatDivider(isDarkMode)
        StatItem(
            icon = Icons.Filled.Stars,
            value = points.toString(),
            label = "Points",
            isDarkMode = isDarkMode
        )
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    isDarkMode: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = AppColors.SkyBrand
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isDarkMode) Color.White else AppColors.NavyDeep
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.textSecondary(isDarkMode)
        )
    }
}

@Composable
private fun StatDivider(isDarkMode: Boolean) {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(AppColors.border(isDarkMode).copy(alpha = 0.4f))
    )
}

// ─────────────────────────────────────────────────────────────
// PREFERENCES SECTION
// ─────────────────────────────────────────────────────────────
@Composable
private fun PreferencesSectionCard(
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
    darkModeEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    selectedLanguage: String,
    selectedCurrency: String,
    isDarkMode: Boolean,
    preferReducedMotion: Boolean
) {
    Column {
        PremiumSectionHeader(
            title = "Preferences",
            icon = Icons.Filled.Tune,
            isDarkMode = isDarkMode
        )
        Spacer(Modifier.height(4.dp))
        PremiumSectionCard(isDarkMode = isDarkMode, preferReducedMotion = preferReducedMotion) {
            PremiumToggleRow(
                icon = Icons.Filled.Notifications,
                title = "Notifications",
                subtitle = "Receive booking updates",
                isChecked = notificationsEnabled,
                onToggle = onNotificationsToggle,
                iconTint = AppColors.SkyBrand,
                iconBg = AppColors.SkyBrand.copy(alpha = 0.1f),
                isDarkMode = isDarkMode,
                preferReducedMotion = preferReducedMotion
            )
            PremiumDivider(isDarkMode)
            PremiumSettingsRow(
                icon = Icons.Filled.Language,
                title = "Language",
                subtitle = selectedLanguage,
                iconTint = AppColors.GoldPrimary,
                iconBg = AppColors.GoldPrimary.copy(alpha = 0.1f),
                onClick = { },
                isDarkMode = isDarkMode,
                preferReducedMotion = preferReducedMotion
            )
            PremiumDivider(isDarkMode)
            PremiumSettingsRow(
                icon = Icons.Filled.AttachMoney,
                title = "Currency",
                subtitle = selectedCurrency,
                iconTint = AppColors.GoldPrimary,
                iconBg = AppColors.GoldPrimary.copy(alpha = 0.1f),
                onClick = { },
                isDarkMode = isDarkMode,
                preferReducedMotion = preferReducedMotion
            )
            PremiumDivider(isDarkMode)
            PremiumToggleRow(
                icon = Icons.Filled.DarkMode,
                title = "Dark Mode",
                subtitle = "Switch app appearance",
                isChecked = darkModeEnabled,
                onToggle = onDarkModeToggle,
                iconTint = AppColors.NavyMid,
                iconBg = AppColors.NavyMid.copy(alpha = 0.15f),
                isDarkMode = isDarkMode,
                preferReducedMotion = preferReducedMotion
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// SUPPORT SECTION
// ─────────────────────────────────────────────────────────────
@Composable
private fun SupportSectionCard(
    isDarkMode: Boolean,
    preferReducedMotion: Boolean
) {
    Column {
        PremiumSectionHeader(
            title = "Support & About",
            icon = Icons.Filled.Help,
            isDarkMode = isDarkMode
        )
        Spacer(Modifier.height(4.dp))
        PremiumSectionCard(isDarkMode = isDarkMode, preferReducedMotion = preferReducedMotion) {
            PremiumSettingsRow(
                icon = Icons.Filled.HelpCenter,
                title = "Help Center",
                subtitle = "FAQs and support articles",
                iconTint = AppColors.SkyBrand,
                iconBg = AppColors.SkyBrand.copy(alpha = 0.1f),
                onClick = { },
                isDarkMode = isDarkMode,
                preferReducedMotion = preferReducedMotion
            )
            PremiumDivider(isDarkMode)
            PremiumSettingsRow(
                icon = Icons.Filled.Description,
                title = "Terms of Service",
                subtitle = "Read our usage terms",
                iconTint = AppColors.textSecondary(isDarkMode),
                iconBg = AppColors.textSecondary(isDarkMode).copy(alpha = 0.1f),
                onClick = { },
                isDarkMode = isDarkMode,
                preferReducedMotion = preferReducedMotion
            )
            PremiumDivider(isDarkMode)
            PremiumSettingsRow(
                icon = Icons.Filled.Policy,
                title = "Privacy Policy",
                subtitle = "How we protect your data",
                iconTint = AppColors.textSecondary(isDarkMode),
                iconBg = AppColors.textSecondary(isDarkMode).copy(alpha = 0.1f),
                onClick = { },
                isDarkMode = isDarkMode,
                preferReducedMotion = preferReducedMotion
            )
            PremiumDivider(isDarkMode)
            PremiumInfoRow(
                icon = Icons.Filled.Info,
                label = "App Version",
                value = "1.0.0",
                iconTint = AppColors.textTertiary(isDarkMode),
                iconBg = AppColors.textTertiary(isDarkMode).copy(alpha = 0.1f),
                isDarkMode = isDarkMode
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// SHIMMER BRUSH & SKELETON
// ─────────────────────────────────────────────────────────────
@Composable
private fun shimmerBrush(isDarkMode: Boolean): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_progress"
    )
    val shimmerBase = if (isDarkMode) {
        listOf(
            AppColors.DarkBorder.copy(alpha = 0.2f),
            AppColors.DarkBorder.copy(alpha = 0.5f),
            AppColors.DarkBorder.copy(alpha = 0.2f)
        )
    } else {
        listOf(
            Color.LightGray.copy(alpha = 0.3f),
            Color.LightGray.copy(alpha = 0.7f),
            Color.LightGray.copy(alpha = 0.3f)
        )
    }
    return Brush.linearGradient(
        colors = shimmerBase,
        start = Offset(progress * 1000f - 200f, 0f),
        end = Offset(progress * 1000f, 0f)
    )
}

@Composable
private fun PremiumInfoRowSkeleton(isDarkMode: Boolean) {
    val brush = shimmerBrush(isDarkMode)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(brush)
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(9.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// SECTION COMPONENTS
// ─────────────────────────────────────────────────────────────
@Composable
private fun PremiumSectionHeader(
    title: String,
    icon: ImageVector,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = AppColors.textTertiary(isDarkMode),
            modifier = Modifier.size(14.dp)
        )
        Text(
            title.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textTertiary(isDarkMode),
            letterSpacing = 1.2.sp
        )
    }
}

@Composable
private fun PremiumSectionCard(
    isDarkMode: Boolean,
    preferReducedMotion: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = if (isDarkMode) 0.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = AppColors.NavyDeep.copy(alpha = 0.08f),
                spotColor = AppColors.NavyDeep.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.animateContentSize(
                animationSpec = if (preferReducedMotion) tween(0) else spring()
            ),
            content = content
        )
    }
}

@Composable
private fun PremiumDivider(isDarkMode: Boolean) {
    HorizontalDivider(
        modifier = Modifier.padding(start = 74.dp, end = 16.dp),
        thickness = 0.5.dp,
        color = AppColors.border(isDarkMode).copy(alpha = 0.6f)
    )
}

// ─────────────────────────────────────────────────────────────
// INFO ROW (read-only)
// ─────────────────────────────────────────────────────────────
@Composable
private fun PremiumInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color,
    iconBg: Color,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(20.dp), iconTint)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.textTertiary(isDarkMode),
                letterSpacing = 0.8.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.textPrimary(isDarkMode)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TOGGLE ROW
// ─────────────────────────────────────────────────────────────
@Composable
private fun PremiumToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    iconTint: Color,
    iconBg: Color,
    isDarkMode: Boolean,
    preferReducedMotion: Boolean
) {
    val thumbOffset by animateFloatAsState(
        targetValue = if (isChecked) 18f else 0f,
        animationSpec = if (preferReducedMotion) tween(0) else spring(stiffness = Spring.StiffnessHigh),
        label = "toggle_thumb"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isChecked) }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(20.dp), iconTint)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.textPrimary(isDarkMode)
            )
            Spacer(Modifier.height(1.dp))
            Text(
                subtitle,
                fontSize = 11.sp,
                color = AppColors.textSecondary(isDarkMode)
            )
        }
        Spacer(Modifier.width(8.dp))
        // Custom toggle switch: sky/gold
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    if (isChecked) AppColors.SkyBrand else AppColors.border(isDarkMode)
                )
                .padding(horizontal = 3.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .shadow(elevation = 2.dp, shape = CircleShape)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// EDITABLE NAME ROW
// ─────────────────────────────────────────────────────────────
@Composable
private fun PremiumEditableNameRow(
    value: String,
    isEditing: Boolean,
    isLoading: Boolean,
    onEditClick: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onValueChange: (String) -> Unit,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppColors.SkyBrand.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, null, Modifier.size(20.dp), AppColors.SkyBrand)
        }
        Spacer(Modifier.width(14.dp))

        if (isEditing) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "FULL NAME",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textTertiary(isDarkMode),
                    letterSpacing = 0.8.sp
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 15.sp,
                        color = AppColors.textPrimary(isDarkMode)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.SkyBrand,
                        unfocusedBorderColor = AppColors.border(isDarkMode),
                        cursorColor = AppColors.SkyBrand,
                        focusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent,
                        unfocusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (isLoading) {
                            CircularProgressIndicator(
                                Modifier.size(16.dp),
                                color = AppColors.SkyBrand,
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onSave() })
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onSave,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.SkyBrand
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        enabled = !isLoading
                    ) {
                        Text("Save", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppColors.textSecondary(isDarkMode)
                        )
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }
                }
            }
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "FULL NAME",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textTertiary(isDarkMode),
                    letterSpacing = 0.8.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    value.ifEmpty { "Tap to set name" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.textPrimary(isDarkMode)
                )
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Filled.Edit,
                    "Edit",
                    Modifier.size(16.dp),
                    AppColors.SkyBrand
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// SETTINGS ROW
// ─────────────────────────────────────────────────────────────
@Composable
private fun PremiumSettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit,
    isDarkMode: Boolean,
    preferReducedMotion: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = if (preferReducedMotion) tween(0) else spring(stiffness = Spring.StiffnessHigh),
        label = "settings_row_press"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(20.dp), iconTint)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.textPrimary(isDarkMode)
            )
            Spacer(Modifier.height(1.dp))
            Text(
                subtitle,
                fontSize = 12.sp,
                color = AppColors.textSecondary(isDarkMode)
            )
        }
        Icon(
            Icons.Filled.ChevronRight,
            null,
            Modifier.size(18.dp),
            AppColors.textTertiary(isDarkMode)
        )
    }
}

// ─────────────────────────────────────────────────────────────
// SIGN OUT BUTTON
// ─────────────────────────────────────────────────────────────
@Composable
private fun SignOutButton(
    onSignOut: () -> Unit,
    isDarkMode: Boolean,
    preferReducedMotion: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = if (preferReducedMotion) tween(0) else spring(stiffness = Spring.StiffnessHigh),
        label = "signout_press"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(AppColors.Error.copy(alpha = 0.06f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSignOut
            )
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                tint = AppColors.Error,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Sign Out",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = AppColors.Error
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// FAVORITE HOTEL CARD
// ─────────────────────────────────────────────────────────────
@Composable
private fun FavoriteHotelCard(
    hotel: Hotel,
    isDarkMode: Boolean,
    preferReducedMotion: Boolean
) {
    var deleteConfirmPending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Auto-cancel delete confirm after 2s
    LaunchedEffect(deleteConfirmPending) {
        if (deleteConfirmPending) {
            delay(2000)
            deleteConfirmPending = false
        }
    }

    val deleteScale by animateFloatAsState(
        targetValue = if (deleteConfirmPending) 1.1f else 1f,
        animationSpec = if (preferReducedMotion) tween(0) else spring(stiffness = Spring.StiffnessHigh),
        label = "delete_confirm_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDarkMode) 0.dp else 4.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = AppColors.NavyDeep.copy(alpha = 0.06f),
                spotColor = AppColors.NavyDeep.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hotel image with bottom gradient overlay
            Box {
                AsyncImage(
                    model = hotel.imageUrl,
                    contentDescription = hotel.title,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
                // Bottom gradient overlay
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.15f))
                            )
                        )
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    hotel.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary(isDarkMode),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                // Category badge
                Text(
                    hotel.category,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.SkyBrand,
                    letterSpacing = 0.3.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        null,
                        Modifier.size(11.dp),
                        AppColors.textTertiary(isDarkMode)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        hotel.location,
                        fontSize = 11.sp,
                        color = AppColors.textSecondary(isDarkMode),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        null,
                        Modifier.size(11.dp),
                        Color(0xFFFFB800)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        if (hotel.rating > 0) String.format(Locale.US, "%.1f", hotel.rating) else "—",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.textPrimary(isDarkMode)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "per night",
                        fontSize = 10.sp,
                        color = AppColors.textTertiary(isDarkMode)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "$${hotel.price.toInt()}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.GoldPrimary
                )
            }

            Spacer(Modifier.width(8.dp))

            // Delete button with 2-tap confirm
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .graphicsLayer { scaleX = deleteScale; scaleY = deleteScale }
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (deleteConfirmPending) AppColors.Warning.copy(alpha = 0.15f)
                            else AppColors.Error.copy(alpha = 0.08f)
                        )
                        .clickable {
                            if (deleteConfirmPending) {
                                FavoritesViewModel.removeFavorite(hotel.id)
                                deleteConfirmPending = false
                            } else {
                                deleteConfirmPending = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (deleteConfirmPending) Icons.Filled.DeleteOutline else Icons.Filled.Delete,
                        if (deleteConfirmPending) "Confirm delete" else "Remove",
                        Modifier.size(16.dp),
                        if (deleteConfirmPending) AppColors.Warning else AppColors.Error
                    )
                }
                if (deleteConfirmPending) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        "Tap again",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Warning
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// CHANGE PASSWORD BOTTOM SHEET
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (oldPassword: String, newPassword: String) -> Unit,
    isLoading: Boolean,
    isDarkMode: Boolean
) {
    val focusManager = LocalFocusManager.current
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showOldPass by remember { mutableStateOf(false) }
    var showNewPass by remember { mutableStateOf(false) }
    var showConfirmPass by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = AppColors.surface(isDarkMode),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
                    .background(AppColors.textTertiary(isDarkMode))
            )

            Column {
                Text(
                    "Change Password",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary(isDarkMode)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "Your new password must differ from your current one.",
                    fontSize = 13.sp,
                    color = AppColors.textSecondary(isDarkMode)
                )
            }

            PasswordField(
                label = "Current Password",
                value = oldPassword,
                onValueChange = { oldPassword = it },
                showPassword = showOldPass,
                onToggleShow = { showOldPass = !showOldPass },
                placeholder = "Enter current password",
                imeAction = ImeAction.Next,
                isError = false,
                isDarkMode = isDarkMode
            )

            PasswordField(
                label = "New Password",
                value = newPassword,
                onValueChange = { newPassword = it },
                showPassword = showNewPass,
                onToggleShow = { showNewPass = !showNewPass },
                placeholder = "Enter new password",
                imeAction = ImeAction.Next,
                isError = false,
                isDarkMode = isDarkMode
            )

            Column {
                PasswordField(
                    label = "Confirm New Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    showPassword = showConfirmPass,
                    onToggleShow = { showConfirmPass = !showConfirmPass },
                    placeholder = "Re-enter new password",
                    imeAction = ImeAction.Done,
                    isError = validationError != null,
                    isDarkMode = isDarkMode,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                if (validationError != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(validationError!!, fontSize = 12.sp, color = AppColors.Error)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.textSecondary(isDarkMode)
                    )
                ) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = {
                        validationError = when {
                            oldPassword.isBlank() -> "Current password is required"
                            newPassword.length < 6 -> "New password must be at least 6 characters"
                            newPassword != confirmPassword -> "Passwords do not match"
                            else -> null
                        }
                        if (validationError == null) {
                            focusManager.clearFocus()
                            onConfirm(oldPassword, newPassword)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.SkyBrand),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Update", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    showPassword: Boolean,
    onToggleShow: () -> Unit,
    placeholder: String,
    imeAction: ImeAction,
    isError: Boolean,
    isDarkMode: Boolean,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    Column {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textSecondary(isDarkMode)
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder, color = AppColors.textTertiary(isDarkMode)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) AppColors.Error else AppColors.SkyBrand,
                unfocusedBorderColor = if (isError) AppColors.Error else AppColors.border(isDarkMode),
                cursorColor = AppColors.SkyBrand,
                focusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent,
                unfocusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent,
                focusedTextColor = AppColors.textPrimary(isDarkMode),
                unfocusedTextColor = AppColors.textPrimary(isDarkMode)
            ),
            shape = RoundedCornerShape(14.dp),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleShow) {
                    Icon(
                        if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showPassword) "Hide" else "Show",
                        tint = AppColors.textTertiary(isDarkMode)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            isError = isError
        )
    }
}
