package com.hotelbooking.app.ui.screens.profile

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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

    // Sync editedName when profile finishes loading (keyed by userId so it fires after every fetch)
    LaunchedEffect(state.profile?.userId) {
        state.profile?.let { editedName = it.fullName }
    }

    // Toast for errors
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // Toast for action messages
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

    Scaffold(
        containerColor = AppColors.background(isDarkMode),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profile Settings",
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary(isDarkMode)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.textPrimary(isDarkMode)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.surface(isDarkMode)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }

            // ── Profile Header Card ──────────────────────────────────
            item {
                ProfileHeaderCard(
                    name = state.profile?.fullName ?: "Loading...",
                    email = state.profile?.email ?: "",
                    isLoading = state.isLoading && state.profile == null,
                    isDarkMode = isDarkMode
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // ── Account Information Section ──────────────────────────
            item {
                SectionTitle("Account Information", isDarkMode)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.card(isDarkMode)),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkMode) 0.dp else 2.dp)
                ) {
                    Column {
                        InfoRow(
                            icon = Icons.Filled.Email,
                            label = "Email",
                            value = state.profile?.email ?: "—",
                            isEditable = false,
                            isDarkMode = isDarkMode
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = AppColors.border(isDarkMode),
                            thickness = 0.5.dp
                        )
                        InfoRow(
                            icon = Icons.Filled.Phone,
                            label = "Phone",
                            value = state.profile?.phone?.ifEmpty { "Not set" } ?: "Not set",
                            isEditable = false,
                            isDarkMode = isDarkMode
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = AppColors.border(isDarkMode),
                            thickness = 0.5.dp
                        )
                        EditableInfoRow(
                            icon = Icons.Filled.Person,
                            label = "Full Name",
                            value = editedName,
                            isEditing = isEditingName,
                            isLoading = state.isLoading,
                            onEditClick = {
                                editedName = state.profile?.fullName ?: ""
                                isEditingName = true
                            },
                            onSave = {
                                scope.launch {
                                    viewModel.updateName(editedName)
                                }
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

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // ── Privacy & Security Section ───────────────────────────
            item {
                SectionTitle("Privacy & Security", isDarkMode)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.card(isDarkMode)),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkMode) 0.dp else 2.dp)
                ) {
                    SettingsRow(
                        icon = Icons.Filled.Lock,
                        title = "Change Password",
                        subtitle = "Update your account password",
                        onClick = { showChangePasswordSheet = true },
                        isDarkMode = isDarkMode,
                        showChevron = true
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // ── Favorites Section ─────────────────────────────────────
            item {
                SectionTitle("My Favorites", isDarkMode)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (favoritesState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppColors.CyanMain,
                            strokeWidth = 2.dp
                        )
                    }
                }
            } else if (favoritesState.favorites.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.card(isDarkMode)),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkMode) 0.dp else 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = AppColors.textTertiary(isDarkMode)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No favorites yet",
                                fontSize = 14.sp,
                                color = AppColors.textSecondary(isDarkMode)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Tap the heart icon on hotels to save them here",
                                fontSize = 12.sp,
                                color = AppColors.textTertiary(isDarkMode)
                            )
                        }
                    }
                }
            } else {
                items(favoritesState.favorites) { hotel ->
                    FavoriteHotelCard(
                        hotel = hotel,
                        isDarkMode = isDarkMode
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // ── Logout Button ────────────────────────────────────────
            item {
                OutlinedButton(
                    onClick = {
                        TokenManager.clearToken()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AppColors.Error
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(AppColors.Error.copy(alpha = 0.5f))
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = AppColors.Error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Sign Out",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = AppColors.Error
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
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
// Profile Header Card
// ─────────────────────────────────────────────────────────────
@Composable
private fun ProfileHeaderCard(
    name: String,
    email: String,
    isLoading: Boolean,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) AppColors.DarkCard else AppColors.CyanMain.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkMode) 0.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkMode) AppColors.DarkElevated
                        else AppColors.CyanSurface
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = AppColors.CyanMain,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = AppColors.CyanMain
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isDarkMode) AppColors.DarkElevated
                            else AppColors.border(isDarkMode)
                        )
                )
            } else {
                Text(
                    name.ifEmpty { "No Name Set" },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary(isDarkMode)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (!isLoading && email.isNotEmpty()) {
                Text(
                    email,
                    fontSize = 13.sp,
                    color = AppColors.textSecondary(isDarkMode)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Member badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isDarkMode) AppColors.CyanMain.copy(alpha = 0.15f)
                        else AppColors.CyanMain.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Stars,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = AppColors.Gold
                    )
                    Text(
                        "Member",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) AppColors.CyanMain else AppColors.CyanMain
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Section Title
// ─────────────────────────────────────────────────────────────
@Composable
private fun SectionTitle(title: String, isDarkMode: Boolean) {
    Text(
        title,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = AppColors.textPrimary(isDarkMode),
        letterSpacing = 0.3.sp
    )
}

// ─────────────────────────────────────────────────────────────
// Info Row (read-only)
// ─────────────────────────────────────────────────────────────
@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    isEditable: Boolean,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isDarkMode) AppColors.CyanMain.copy(alpha = 0.12f)
                    else AppColors.CyanSurface
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = AppColors.CyanMain
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.textTertiary(isDarkMode),
                letterSpacing = 0.3.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
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
// Editable Info Row (for Full Name)
// ─────────────────────────────────────────────────────────────
@Composable
private fun EditableInfoRow(
    icon: ImageVector,
    label: String,
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
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isDarkMode) AppColors.CyanMain.copy(alpha = 0.12f)
                    else AppColors.CyanSurface
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = AppColors.CyanMain
            )
        }
        Spacer(modifier = Modifier.width(14.dp))

        if (isEditing) {
            // Editing mode: text field + save/cancel buttons
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textTertiary(isDarkMode),
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 15.sp,
                        color = AppColors.textPrimary(isDarkMode)
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.CyanMain,
                        unfocusedBorderColor = AppColors.border(isDarkMode),
                        cursorColor = AppColors.CyanMain,
                        focusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent,
                        unfocusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = AppColors.CyanMain,
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onSave() })
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onSave,
                        modifier = Modifier.height(34.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.CyanMain),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        enabled = !isLoading
                    ) {
                        Text("Save", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            // View mode
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textTertiary(isDarkMode),
                    letterSpacing = 0.3.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    value.ifEmpty { "Tap to add" },
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
                    contentDescription = "Edit",
                    modifier = Modifier.size(18.dp),
                    tint = AppColors.CyanMain
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Settings Row (generic clickable row)
// ─────────────────────────────────────────────────────────────
@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDarkMode: Boolean,
    showChevron: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isDarkMode) AppColors.Gold.copy(alpha = 0.12f)
                    else AppColors.Gold.copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = AppColors.Gold
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.textPrimary(isDarkMode)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                subtitle,
                fontSize = 12.sp,
                color = AppColors.textSecondary(isDarkMode)
            )
        }
        if (showChevron) {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = AppColors.textTertiary(isDarkMode)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Favorite Hotel Card
// ─────────────────────────────────────────────────────────────
@Composable
private fun FavoriteHotelCard(
    hotel: Hotel,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.card(isDarkMode)),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkMode) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hotel image
            AsyncImage(
                model = hotel.imageUrl,
                contentDescription = hotel.title,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            // Hotel info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    hotel.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textPrimary(isDarkMode),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = AppColors.textTertiary(isDarkMode)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        hotel.location,
                        fontSize = 12.sp,
                        color = AppColors.textSecondary(isDarkMode),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFFFFB800)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        if (hotel.rating > 0) String.format(java.util.Locale.US, "%.1f", hotel.rating) else "—",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.textPrimary(isDarkMode)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "$${hotel.price.toInt()}/night",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.CyanMain
                    )
                }
            }
            // Remove button
            IconButton(
                onClick = { FavoritesViewModel.removeFavorite(hotel.id) },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(AppColors.Error.copy(alpha = 0.1f))
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Remove",
                    modifier = Modifier.size(16.dp),
                    tint = AppColors.Error
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// Change Password Bottom Sheet
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
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Handle bar
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
                    .background(AppColors.textTertiary(isDarkMode))
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Title
            Text(
                "Change Password",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary(isDarkMode)
            )

            Text(
                "Ensure your new password is different from your current password.",
                fontSize = 13.sp,
                color = AppColors.textSecondary(isDarkMode)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Old Password
            Column {
                Text(
                    "Current Password",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textSecondary(isDarkMode)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Enter current password", color = AppColors.textTertiary(isDarkMode)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.CyanMain,
                        unfocusedBorderColor = AppColors.border(isDarkMode),
                        cursorColor = AppColors.CyanMain,
                        focusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent,
                        unfocusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent,
                        focusedTextColor = AppColors.textPrimary(isDarkMode),
                        unfocusedTextColor = AppColors.textPrimary(isDarkMode)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    visualTransformation = if (showOldPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showOldPass = !showOldPass }) {
                            Icon(
                                if (showOldPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showOldPass) "Hide" else "Show",
                                tint = AppColors.textTertiary(isDarkMode)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )
            }

            // New Password
            Column {
                Text(
                    "New Password",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textSecondary(isDarkMode)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Enter new password", color = AppColors.textTertiary(isDarkMode)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.CyanMain,
                        unfocusedBorderColor = AppColors.border(isDarkMode),
                        cursorColor = AppColors.CyanMain,
                        focusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent,
                        unfocusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent,
                        focusedTextColor = AppColors.textPrimary(isDarkMode),
                        unfocusedTextColor = AppColors.textPrimary(isDarkMode)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    visualTransformation = if (showNewPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showNewPass = !showNewPass }) {
                            Icon(
                                if (showNewPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showNewPass) "Hide" else "Show",
                                tint = AppColors.textTertiary(isDarkMode)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )
            }

            // Confirm Password
            Column {
                Text(
                    "Confirm New Password",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.textSecondary(isDarkMode)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Re-enter new password", color = AppColors.textTertiary(isDarkMode)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (validationError != null) AppColors.Error else AppColors.CyanMain,
                        unfocusedBorderColor = if (validationError != null) AppColors.Error else AppColors.border(isDarkMode),
                        cursorColor = AppColors.CyanMain,
                        focusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent,
                        unfocusedContainerColor = if (isDarkMode) AppColors.DarkElevated else Color.Transparent,
                        focusedTextColor = AppColors.textPrimary(isDarkMode),
                        unfocusedTextColor = AppColors.textPrimary(isDarkMode)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    visualTransformation = if (showConfirmPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPass = !showConfirmPass }) {
                            Icon(
                                if (showConfirmPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showConfirmPass) "Hide" else "Show",
                                tint = AppColors.textTertiary(isDarkMode)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    isError = validationError != null
                )
                if (validationError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        validationError!!,
                        fontSize = 12.sp,
                        color = AppColors.Error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(50.dp),
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
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.CyanMain),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
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
