package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hotelbooking.app.ui.navigation.Routes
import com.hotelbooking.app.ui.screens.auth.components.*
import com.hotelbooking.app.ui.theme.AppColors

@Composable
fun CreateNewPasswordScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    isDarkMode: Boolean = false
) {
    val context = LocalContext.current
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val authState = viewModel.authState
    val passwordsMatch = confirmPassword.isEmpty() || confirmPassword == viewModel.newPassword

    // Listen for error state to show Toast
    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    AuthScreenScaffold(isDarkMode = isDarkMode) {
        // Header with logo
        AuthHeader(
            icon = Icons.Filled.Shield,
            title = "Reset Password",
            subtitle = "Create a strong new password",
            isDarkMode = isDarkMode
        )

        Spacer(modifier = Modifier.height(32.dp))

        // New Password field
        AuthTextField(
            value = viewModel.newPassword,
            onValueChange = { viewModel.newPassword = it },
            label = "New Password",
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
                        tint = AppColors.CyanMain,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Confirm Password field
        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            leadingIcon = Icons.Filled.Lock,
            isDarkMode = isDarkMode,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = authState !is AuthState.Loading,
            isError = !passwordsMatch,
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                        tint = AppColors.CyanMain,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        // Password mismatch hint
        if (!passwordsMatch) {
            Text(
                text = "Passwords do not match",
                color = AppColors.Error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Confirm button
        AuthPrimaryButton(
            text = "Confirm",
            onClick = {
                if (viewModel.newPassword == confirmPassword) {
                    viewModel.resetPassword { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                            viewModel.resetState()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }
            },
            isLoading = authState is AuthState.Loading,
            enabled = authState !is AuthState.Loading
                    && viewModel.newPassword.isNotEmpty()
                    && viewModel.newPassword == confirmPassword
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Back button
        TextButton(onClick = { navController.popBackStack() }) {
            Text(
                "Go Back",
                color = AppColors.textSecondary(isDarkMode),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}
