package com.hotelbooking.app.ui.screens.profile.itemprofilesetting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hotelbooking.app.ui.screens.home.CyanMain
import com.hotelbooking.app.ui.screens.profile.ProfileViewModel
import com.hotelbooking.app.ui.screens.profile.ProfileUiState


@Composable
fun EditProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    // Gọi API lấy dữ liệu ngay khi màn hình này được mở lên
    LaunchedEffect(Unit) {
        // !!! LƯU Ý: Nhớ thay đoạn ID này bằng ID thật của Kiet trên Supabase nhé
        val myUserId = "8f187c60-14f3-48f8-a9c9-8f6177d..."
        viewModel.fetchUserProfile(myUserId)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Lắng nghe trạng thái từ Server
        when (val state = viewModel.uiState) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator(color = CyanMain)
            }
            is ProfileUiState.Success -> {
                // Thành công -> Đổ dữ liệu vào UI của bạn
                ProfileSettingItem(
                    fullName = state.user.full_name,
                    email = state.user.email,
                    phone = state.user.phone,
                    createdDate = state.user.created_at.substring(0, 10),
                    password = "****************"
                )
            }
            is ProfileUiState.Error -> {
                Text(text = "Lỗi: ${state.message}", color = androidx.compose.ui.graphics.Color.Red)
            }
        }
    }
}