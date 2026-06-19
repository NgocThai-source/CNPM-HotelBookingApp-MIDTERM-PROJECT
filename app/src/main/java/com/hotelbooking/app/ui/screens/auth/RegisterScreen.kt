package com.hotelbooking.app.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.hotelbooking.app.R
import com.hotelbooking.app.data.model.RegisterRequest
import com.hotelbooking.app.ui.screens.auth.components.*
import com.hotelbooking.app.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    viewModel: AuthViewModel,
    isDarkMode: Boolean = false
) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState = viewModel.authState
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Navigation and error handling
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Error -> {
                snackbarHostState.showSnackbar(authState.message)
                viewModel.resetState()
            }
            is AuthState.Success -> {
                snackbarHostState.showSnackbar(authState.message)
                delay(1000)
                onRegisterSuccess()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LuxuryAuthScaffold(
            isDarkMode = isDarkMode,
            backgroundRes = R.drawable.auth_hero,
            heroContent = {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Hotel Booking App",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Create Account",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Experience premium hotel booking",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        ) {
            // Form section label
            Text(
                text = "Your Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary(isDarkMode),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )

            LuxuryTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full name",
                leadingIcon = Icons.Filled.Person,
                isDarkMode = isDarkMode,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(14.dp))

            LuxuryTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone number",
                leadingIcon = Icons.Filled.Phone,
                isDarkMode = isDarkMode,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(14.dp))

            LuxuryTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email address",
                leadingIcon = Icons.Filled.Email,
                isDarkMode = isDarkMode,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(14.dp))

            LuxuryTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Filled.Lock,
                isDarkMode = isDarkMode,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = authState !is AuthState.Loading,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = AppColors.SkyBrand,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            LuxuryPrimaryButton(
                text = "Create Account",
                onClick = {
                    when {
                        fullName.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank() -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please fill in all fields")
                            }
                        }
                        password.length < 6 -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Password must be at least 6 characters")
                            }
                        }
                        else -> {
                            viewModel.register(
                                RegisterRequest(
                                    email = email.trim(),
                                    password = password,
                                    fullName = fullName.trim(),
                                    phone = phone.trim()
                                )
                            ) { /* navigation handled by LaunchedEffect(authState) */ }
                        }
                    }
                },
                isLoading = authState is AuthState.Loading,
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthFooterLink(
                normalText = "Already have an account? ",
                linkText = "Sign In",
                onClick = onBackToLogin,
                isDarkMode = isDarkMode
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Snackbar overlaid on top of everything
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(1f)
                .padding(bottom = 16.dp)
        )
    }
}
