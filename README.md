# Kaushalya Karnataka 🔧
### GenAI-Powered Skill Showcase Platform for Blue-Collar Workers
**Project 92 · Android App Development Using Generative AI · MindMatrix**

---

## What This App Does

Kaushalya Karnataka (Kannada: *Skill of Karnataka*) is a full-stack Android application that empowers blue-collar workers — electricians, plumbers, carpenters, painters, tailors — across Karnataka to:

- Build a professional digital portfolio with an **AI-generated bio** (Gemini 1.5 Pro)
- List **Service Cards** with transparent pricing
- Upload a **work photo gallery**
- Receive **Hire Me** requests from local customers via push notification (FCM)
- Earn trust through a **community Review Wall** with star ratings
- Appear in **category-based local search**

---

## Tech Stack (as per PDR §8.1)

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture (Repository pattern) |
| Cloud DB | Firebase Firestore |
| Auth | Firebase Auth (Phone OTP) |
| Image Storage | Firebase Storage |
| AI | Gemini 1.5 Pro API (via Retrofit) |
| Networking | Retrofit 2 + OkHttp |
| Local Cache | Room (SQLite) |
| DI | Hilt (Dagger) |
| Async | Kotlin Coroutines + StateFlow |
| Images | Coil |
| Push Notifications | Firebase Cloud Messaging (FCM) |

---

## Project Structure

```
app/src/main/java/com/kaushalya/karnataka/
├── data/
│   ├── model/          Models.kt          – Worker, ServiceCard, Review, HireRequest, WorkPhoto
│   ├── remote/         GeminiApiService.kt – Retrofit service + prompt builder
│   └── repository/     WorkerRepository.kt – Single source of truth for all Firestore + Gemini ops
├── di/                 AppModule.kt        – Hilt providers for Firestore, Auth, Retrofit
├── domain/             (use-case layer; extend here)
├── ui/
│   ├── components/     SharedComponents.kt – WorkerCard, StarRow, ServiceCardItem, StatusBadge
│   ├── navigation/     Navigation.kt       – Screen sealed class + BottomNavItem
│   ├── screens/
│   │   ├── home/       HomeScreen.kt       – Feed + search + category chips
│   │   ├── profile/    ProfileScreen.kt    – Hero + tabs (Services / Gallery / Reviews)
│   │   ├── aibuilder/  AiBuilderScreen.kt  – 4-step Q&A + Gemini + typewriter animation
│   │   └── dashboard/  DashboardScreen.kt  – Stats + hire requests + availability toggle
│   └── theme/          Color.kt / Theme.kt – PDR colour palette + Material 3 scheme
├── viewmodel/
│   ├── HomeViewModel.kt
│   ├── ProfileViewModel.kt
│   ├── AiBioViewModel.kt
│   └── DashboardViewModel.kt
├── KaushalyaApp.kt     – @HiltAndroidApp
├── MainActivity.kt     – NavHost + bottom navigation
└── KaushalyaMessagingService.kt – FCM push notifications
```

---

## Setup Instructions

### Step 1 — Clone & open in Android Studio

Open the `KaushalyaKarnataka/` folder in **Android Studio Hedgehog (2023.1.1)** or newer.

### Step 2 — Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com) → **Add project**
2. Add an Android app with package name `com.kaushalya.karnataka`
3. Download `google-services.json` and place it in `app/`
4. Enable the following in Firebase Console:
   - **Authentication** → Phone number sign-in
   - **Firestore Database** → Start in test mode
   - **Storage** → Start in test mode
   - **Cloud Messaging** → (auto-enabled)

### Step 3 — Gemini API Key

1. Get your key from [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Copy `local.properties.template` → `local.properties`
3. Replace `YOUR_GEMINI_API_KEY_HERE` with your actual key

```properties
GEMINI_API_KEY=AIzaSy...your_key_here
```

### Step 4 — Build & Run

```bash
./gradlew assembleDebug
```

Or press ▶ in Android Studio. Target a device/emulator running **Android 7.0+ (API 24)**.

---

## Firestore Collections (PDR §10)

| Collection | Key Fields |
|------------|------------|
| `workers` | workerId, fullName, skillCategory, locationText, bio, avgRating |
| `service_cards` | cardId, workerId, title, price, priceType |
| `reviews` | reviewId, workerId, customerName, rating, reviewText |
| `hire_requests` | requestId, workerId, customerName, customerPhone, status |
| `work_photos` | photoId, workerId, photoUrl, caption |

---

## Firestore Security Rules (paste into Firebase Console)

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Workers can write their own profile; everyone can read
    match /workers/{workerId} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == workerId;
    }
    // Anyone authenticated can add reviews and hire requests
    match /reviews/{id}      { allow read: if true; allow create: if request.auth != null; }
    match /hire_requests/{id}{ allow read, write: if request.auth != null; }
    match /service_cards/{id}{ allow read: if true; allow write: if request.auth != null; }
    match /work_photos/{id}  { allow read: if true; allow write: if request.auth != null; }
  }
}
```

---

## Colour Palette (PDR §12.2)

| Role | Hex |
|------|-----|
| Primary (Royal Blue) | `#1A2B5F` |
| Secondary (Electric Blue) | `#4169E1` |
| Accent (Saffron Orange) | `#E8630A` |
| Highlight (Warm Gold) | `#F4A820` |
| Background (Off White) | `#F8F9FF` |

---

## Success Criteria Coverage (PDR §15)

| SC | Criterion | Implementation |
|----|-----------|---------------|
| SC-01 | Service Card editable anytime | `ProfileViewModel.saveServiceCard()` → Firestore update |
| SC-02 | Search filters by category | `HomeViewModel` — `derivedStateOf` on category + query |
| SC-03 | Avg rating auto-calculated | `WorkerRepository.addReview()` recalculates & writes to worker doc |
| SC-04 | Simple, accessible UI | Material 3 + 48dp touch targets + icon+text nav labels |
| SC-05 | Gemini bio generation | `AiBuilderScreen` → `AiBioViewModel` → `GeminiApiService` |
| SC-06 | Offline browsing | Firestore offline persistence (enabled by default) |

---

## Adding Seed Data (for demo / presentation)

Run this in the Firebase Console → Firestore → **Start collection**:

**Collection:** `workers`  
**Document ID:** `demo_worker_01`

```json
{
  "workerId": "demo_worker_01",
  "fullName": "Raju Nayak",
  "skillCategory": "Electrician",
  "locationText": "Jayanagar, Bengaluru",
  "bio": "Certified electrician with 9 years of experience in residential wiring, panel upgrades, and fan installations. Known for clean workmanship and punctuality.",
  "avgRating": 4.8,
  "totalReviews": 34,
  "isAvailable": true,
  "phoneNumber": "+91 98765 43210"
}
```

Add 4–5 such documents with different skill categories to populate the home feed.

---

## Presentation Checklist

- [ ] `google-services.json` placed in `app/`
- [ ] `GEMINI_API_KEY` set in `local.properties`
- [ ] Firestore rules applied
- [ ] Seed worker documents added
- [ ] App builds and runs on physical/emulator device
- [ ] Demonstrate: search → filter by category → open profile → Hire Me → AI Builder → Dashboard

---

*Kaushalya Karnataka · Project 92 · Android App Development Using Generative AI · MindMatrix*
