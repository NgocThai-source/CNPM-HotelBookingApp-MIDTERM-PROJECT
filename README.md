# 🏨 Hotel Booking App

> Ứng dụng đặt phòng khách sạn trên Android — Dự án môn Công nghệ Phần mềm

## 📌 Giới thiệu

Hotel Booking App là ứng dụng Android cho phép người dùng tìm kiếm, đặt phòng và quản lý đặt chỗ khách sạn. Ứng dụng hỗ trợ hai loại người dùng:

- **Khách hàng (Customer)**: Tìm kiếm khách sạn, xem chi tiết, đặt phòng, chat với quản lý
- **Quản lý khách sạn (Hotel Manager)**: Quản lý danh sách khách sạn, phòng, đặt chỗ, giao tiếp với khách

## 🛠️ Công nghệ sử dụng

| Thành phần | Công nghệ |
|-----------|-----------|
| Ngôn ngữ | Kotlin |
| UI Framework | Jetpack Compose + Material Design 3 |
| Backend | Firebase (Auth, Firestore, FCM) |
| Database | Firebase Firestore (NoSQL) |
| Architecture | MVVM + Repository Pattern |
| DI | Hilt |
| IDE | Android Studio |

## 🚀 Cài đặt & Chạy dự án

### Yêu cầu
- Android Studio Ladybug (2024.2.1) trở lên
- JDK 17
- Android SDK 35
- Tài khoản Firebase

### Các bước

1. **Clone repository**
   ```bash
   git clone <repository-url>
   cd HotelBookingApp
   ```

2. **Cấu hình Firebase**
   - Tạo project trên [Firebase Console](https://console.firebase.google.com/)
   - Kích hoạt: Authentication (Email/Password), Firestore Database, Cloud Messaging
   - Tải file `google-services.json` và đặt vào thư mục `app/`

3. **Mở trong Android Studio**
   - File → Open → Chọn thư mục `HotelBookingApp`
   - Đợi Gradle sync hoàn tất

4. **Chạy ứng dụng**
   - Chọn thiết bị/emulator (API 26+)
   - Nhấn Run ▶️

## 📱 Tính năng chính

- ✅ Đăng ký / Đăng nhập (Firebase Auth)
- ✅ Tìm kiếm & lọc khách sạn
- ✅ Xem chi tiết khách sạn (hình ảnh, giá, tiện ích, đánh giá)
- ✅ Đặt phòng khách sạn
- ✅ Quản lý đặt chỗ (xem, hủy)
- ✅ Chat real-time giữa khách và quản lý
- ✅ Push notifications (FCM)
- ✅ Quản lý khách sạn (dành cho Manager)
- ✅ Hỗ trợ Dark Mode

## 👥 Nhóm phát triển

| Thành viên | MSSV | Vai trò |
|-----------|------|---------|
| Đoàn Văn Nguyên | 24IT182 | Backend Developer (Firebase) |
| Nguyễn Ngọc Thái | 24IT241 | UI/UX Designer |
| Nguyễn Mạnh Kiên | 24IT127 | Backend Developer (Firebase) |
| Trần Xuân Thức | 24IT267 | Backend Developer (Firebase) |
| Lê Văn Kiệt | 24IT131 | Frontend Developer (Android) |

## 📚 Tài liệu

- [Quy tắc dự án (RULES.md)](./RULES.md)
- [Báo cáo dự án (CNPM.docx)](../CNPM.docx)

## 📄 Giấy phép

Dự án này được phát triển cho mục đích học tập tại Đại học Việt-Hàn (VKU).
