package com.hotelbooking.app.ui.screens.profile

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hotelbooking.app.data.model.Hotel
import com.hotelbooking.app.ui.theme.AppColors
import com.hotelbooking.app.util.TokenManager
import kotlinx.coroutines.launch

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
                    onBack = onBack
                )
            }

            item { Spacer(Modifier.height(20.dp)) }

            // ── ACCOUNT INFORMATION ──────────────────────────────────
            item {
                PremiumSectionHeader(
                    title = "Account",
                    icon = Icons.Filled.Person,
                    isDarkMode = isDarkMode
                )
            }

            item {
                PremiumSectionCard(isDarkMode = isDarkMode) {
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

            item { Spacer(Modifier.height(20.dp)) }

            // ── SECURITY ─────────────────────────────────────────────
            item {
                PremiumSectionHeader(
                    title = "Security",
                    icon = Icons.Filled.Lock,
                    isDarkMode = isDarkMode
                )
            }

            item {
                PremiumSectionCard(isDarkMode = isDarkMode) {
                    PremiumSettingsRow(
                        icon = Icons.Filled.Lock,
                        title = "Change Password",
                        subtitle = "Update your account password",
                        iconTint = AppColors.GoldPrimary,
                        iconBg = AppColors.GoldPrimary.copy(alpha = 0.1f),
                        onClick = { showChangePasswordSheet = true },
                        isDarkMode = isDarkMode
                    )
                }
            }

            item { Spacer(Modifier.height(20.dp)) }

            // ── FAVOURITES ───────────────────────────────────────────
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
                        PremiumSectionCard(isDarkMode = isDarkMode) {
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
                                    "Tap ♥ on hotels to save them here",
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
                            FavoriteHotelCard(hotel = hotel, isDarkMode = isDarkMode)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(28.dp)) }

            // ── LOGOUT ───────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppColors.Error.copy(alpha = 0.06f))
                        .clickable {
                            TokenManager.clearToken()
                            onLogout()
                        }
                        .padding(vertical = 16.dp),
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
    onBack: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(500),
        label = "header_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .graphicsLayer { this.alpha = alpha }
    ) {
        // Navy gradient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
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

            // Avatar with gradient ring
            Box(
                modifier = Modifier
                    .size(92.dp)
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
                }
            }

            Spacer(Modifier.height(12.dp))

            if (!isLoading) {
                Text(
                    text = name.ifEmpty { "Set your name" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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

            Spacer(Modifier.height(12.dp))

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

            Spacer(Modifier.height(16.dp))
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
            .padding(horizontal = 20.dp, vertical = 6.dp),
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
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkMode) 0.dp else 4.dp
        )
    ) {
        Column(content = content)
    }
}

@Composable
private fun PremiumDivider(isDarkMode: Boolean) {
    HorizontalDivider(
        modifier = Modifier.padding(start = 70.dp, end = 16.dp),
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
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(20.dp), iconTint)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.textTertiary(isDarkMode),
                letterSpacing = 0.5.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.textPrimary(isDarkMode)
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
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textTertiary(isDarkMode),
                    letterSpacing = 0.5.sp
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
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textTertiary(isDarkMode),
                    letterSpacing = 0.5.sp
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
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(20.dp), iconTint)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 15.sp,
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
// FAVORITE HOTEL CARD
// ─────────────────────────────────────────────────────────────
@Composable
private fun FavoriteHotelCard(hotel: Hotel, isDarkMode: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppColors.DarkCard else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkMode) 0.dp else 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = hotel.imageUrl,
                contentDescription = hotel.title,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    hotel.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textPrimary(isDarkMode),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
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
                        fontSize = 12.sp,
                        color = AppColors.textSecondary(isDarkMode),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        null,
                        Modifier.size(11.dp),
                        Color(0xFFFFB800)
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        if (hotel.rating > 0)
                            String.format(java.util.Locale.US, "%.1f", hotel.rating)
                        else "—",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.textPrimary(isDarkMode)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "$${hotel.price.toInt()}/night",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.SkyBrand
                    )
                }
            }
            IconButton(
                onClick = { FavoritesViewModel.removeFavorite(hotel.id) },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AppColors.Error.copy(alpha = 0.08f))
            ) {
                Icon(
                    Icons.Filled.Delete,
                    "Remove",
                    Modifier.size(15.dp),
                    AppColors.Error
                )
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

            // Old Password
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

            // New Password
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

            // Confirm Password
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
