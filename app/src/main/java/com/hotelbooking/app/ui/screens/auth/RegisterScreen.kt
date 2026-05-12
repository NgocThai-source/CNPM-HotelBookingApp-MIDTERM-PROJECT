package com.hotelbooking.app.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hotelbooking.app.data.model.RegisterRequest
import com.hotelbooking.app.ui.screens.auth.components.*

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

    // Listen for auth state changes
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            snackbarHostState.showSnackbar(authState.message)
            viewModel.resetState()
        } else if (authState is AuthState.Success) {
            snackbarHostState.showSnackbar(authState.message)
            onRegisterSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AuthColors.background(isDarkMode)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ScrollableAuthScreenScaffold(isDarkMode = isDarkMode) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        AuthHeader(
                            icon = Icons.Filled.PersonAdd,
                            title = "Create Account",
                            subtitle = "Experience premium hotel booking",
                            isDarkMode = isDarkMode
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Full Name field
                    AuthTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Full Name",
                        leadingIcon = Icons.Filled.Person,
                        isDarkMode = isDarkMode,
                        enabled = authState !is AuthState.Loading
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Phone field
                    AuthTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number",
                        leadingIcon = Icons.Filled.Phone,
                        isDarkMode = isDarkMode,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        enabled = authState !is AuthState.Loading
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Email field
                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        leadingIcon = Icons.Filled.Email,
                        isDarkMode = isDarkMode,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = authState !is AuthState.Loading
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password field
                    AuthTextField(
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
                                    tint = AuthColors.CyanMain
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Sign Up button
                    AuthPrimaryButton(
                        text = "Sign Up",
                        onClick = {
                            if (fullName.isNotBlank() && email.isNotBlank() && password.isNotBlank() && phone.isNotBlank()) {
                                val request = RegisterRequest(email, password, fullName, phone)
                                viewModel.register(request) { /* handled by LaunchedEffect */ }
                            }
                        },
                        isLoading = authState is AuthState.Loading,
                        enabled = authState !is AuthState.Loading
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Back to Login link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AuthFooterLink(
                            normalText = "Already have an account? ",
                            linkText = "Sign In",
                            onClick = onBackToLogin,
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }
    }
}
