package com.hotelbooking.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hotelbooking.app.R
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

    LaunchedEffect(authState) {
        if (authState is AuthState.Error) {
            Toast.makeText(context, authState.message, Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    LuxuryAuthScaffold(
        isDarkMode = isDarkMode,
        backgroundRes = R.drawable.auth_background,
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
                text = "Reset Password",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Create a strong new password for your account",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    ) {
        Text(
            text = "New Password",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textPrimary(isDarkMode),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        )

        Text(
            text = "Must be at least 6 characters",
            fontSize = 14.sp,
            color = AppColors.textSecondary(isDarkMode),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        LuxuryTextField(
            value = viewModel.newPassword,
            onValueChange = { viewModel.newPassword = it },
            label = "New password",
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

        Spacer(modifier = Modifier.height(14.dp))

        LuxuryTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm new password",
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
                        tint = if (!passwordsMatch) AppColors.Error else AppColors.SkyBrand,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

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

        LuxuryPrimaryButton(
            text = "Set New Password",
            onClick = {
                if (viewModel.newPassword == confirmPassword) {
                    viewModel.resetPassword { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
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

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Go Back",
                color = AppColors.textSecondary(isDarkMode),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}
