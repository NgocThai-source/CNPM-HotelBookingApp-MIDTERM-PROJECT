<p align="center">
  <img src="https://img.icons8.com/3d-fluency/94/hotel-building.png" alt="Hotel Booking App Logo" width="80"/>
</p>

<h1 align="center">Hotel Booking App</h1>

<p align="center">
  <strong>A modern Android application for seamless hotel search, booking, and management.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
  <img src="https://img.shields.io/badge/Backend-Firebase-FFCA28?logo=firebase&logoColor=black" alt="Firebase"/>
  <img src="https://img.shields.io/badge/Architecture-MVVM-blue" alt="MVVM"/>
</p>

---

## 📌 About The Project

**Hotel Booking App** is a native Android application developed as a **Mid-Term Project** for the *Software Engineering* course at **Vietnam-Korea University of Information and Communication Technology (VKU)**.

The application provides a unified platform where **customers** can discover, compare, and book hotel rooms with ease, while **hotel managers** can efficiently manage their listings, room availability, and customer interactions — all within a single mobile experience.

### 🎯 Problem Statement

The traditional hotel booking process is often fragmented across multiple platforms, forcing users to juggle between different apps to search, compare prices, and make reservations. This leads to inconvenience and a poor user experience.

### 💡 Our Solution

Hotel Booking App consolidates the entire booking journey into one intuitive application:

- **For Customers** — Search hotels, view detailed information, book rooms, chat with hotel staff, and receive instant booking notifications.
- **For Hotel Managers** — Add and manage hotel listings, update room availability, communicate with customers, and track bookings in real time.

---

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| 🔐 **Authentication** | Secure email/password registration and login with role-based access (Customer / Manager) |
| 🔍 **Hotel Search** | Browse and search hotels by city with real-time data synchronization |
| 🏨 **Hotel Details** | View comprehensive hotel information including photos, amenities, ratings, and available rooms |
| 📅 **Room Booking** | Select rooms, choose dates, and confirm reservations with instant status updates |
| 📋 **Booking Management** | View, track, and cancel bookings with color-coded status indicators |
| 💬 **Real-Time Chat** | Direct messaging between customers and hotel managers powered by Firestore |
| 🔔 **Push Notifications** | Instant alerts for booking confirmations and updates via Firebase Cloud Messaging |
| 🏢 **Manager Dashboard** | Complete hotel management tools — add, edit, and delete listings |
| 🌙 **Dark Mode** | Full dark theme support following Material Design 3 guidelines |

---

## 🏗️ Architecture & Tech Stack

### System Architecture

The application follows the **MVVM (Model-View-ViewModel)** pattern combined with the **Repository Pattern** and **Hilt** for dependency injection, ensuring clean separation of concerns and testability.

```
┌──────────────────────────────────────────────┐
│          Presentation Layer (UI)             │
│     Jetpack Compose  ·  Material Design 3    │
│                  ↕ StateFlow                 │
│            ViewModel Layer                   │
│        Business Logic  ·  State Mgmt        │
│                  ↕ Coroutines                │
│            Repository Layer                  │
│         Data Abstraction  ·  Caching         │
│                  ↕ Firebase SDK              │
│              Data Layer                      │
│   Firebase Auth · Firestore · FCM · Storage  │
└──────────────────────────────────────────────┘
```

### Technology Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material Design 3 (Material You) |
| **Navigation** | Jetpack Navigation Compose |
| **State Management** | StateFlow / SharedFlow |
| **Dependency Injection** | Hilt (Dagger) |
| **Authentication** | Firebase Authentication |
| **Database** | Cloud Firestore (NoSQL, Real-Time) |
| **Notifications** | Firebase Cloud Messaging (FCM) |
| **Image Loading** | Coil |
| **Async Operations** | Kotlin Coroutines + Flow |
| **IDE** | Android Studio |
| **Version Control** | Git & GitHub |

---

## 📱 App Screens

| Screen | Description |
|--------|-------------|
| **Login / Register** | User authentication with role selection (Customer or Manager) |
| **Home** | Hotel discovery with search bar, popular listings, and real-time updates |
| **Hotel Detail** | Full hotel information — images, rating, amenities, and available rooms |
| **Booking** | Date selection, guest count, price calculation, and booking confirmation |
| **My Bookings** | Booking history with status tracking (Pending / Confirmed / Cancelled / Completed) |
| **Chat** | Real-time messaging with hotel staff including read receipts |
| **Profile** | User profile, settings, and manager dashboard access |
| **Manager Dashboard** | Hotel CRUD operations and booking management for hotel managers |

---

## 🗄️ Database Design

The application uses **Cloud Firestore** with the following collection structure:

```
├── users/              → User profiles (name, email, role, phone)
├── hotels/             → Hotel listings (name, city, rating, amenities, price)
│   └── rooms/          → Room subcollection (type, price, capacity, availability)
├── bookings/           → Reservations (user, hotel, room, dates, status, price)
└── messages/           → Chat conversations
    └── messages/       → Message subcollection (sender, text, timestamp, read)
```

---

## 🔄 Development Methodology

This project follows the **Scrum** framework with five one-week sprints:

| Sprint | Focus Area |
|--------|-----------|
| Sprint 1 | Project setup, requirements analysis, UI wireframing (Figma) |
| Sprint 2 | User authentication, home screen, hotel browsing UI |
| Sprint 3 | Core features — hotel listing, detail view, booking system |
| Sprint 4 | Advanced features — real-time chat, push notifications |
| Sprint 5 | Testing, bug fixing, documentation, and demo |

---

## 👥 Team Members

| Name | Student ID | Role |
|------|-----------|------|
| **Đoàn Văn Nguyên** | 24IT182 | Backend Developer (Firebase) |
| **Nguyễn Ngọc Thái** | 24IT241 | UI/UX Designer |
| **Nguyễn Mạnh Kiên** | 24IT127 | Backend Developer (Firebase) |
| **Trần Xuân Thức** | 24IT267 | Backend Developer (Firebase) |
| **Lê Văn Kiệt** | 24IT131 | Frontend Developer (Android) |

**Instructor:** Dr. Nguyen Thanh Tuan  
**University:** Vietnam-Korea University of Information and Communication Technology (VKU)  
**Course:** Software Engineering — March 2026

---

## 📚 References

- [Firebase Documentation](https://firebase.google.com/docs)
- [Android Developers Documentation](https://developer.android.com/docs)
- [Jetpack Compose Guide](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [The C4 Model for Software Architecture](https://c4model.com/)

---

<p align="center">
  <sub>Built with ❤️ by <strong>Team VKU</strong> — Software Engineering Mid-Term Project 2026</sub>
</p>
