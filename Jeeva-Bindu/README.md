# Jeeva-Bindu

Jeeva-Bindu is a rapid response blood donor app built with Kotlin, Jetpack Compose, Clean Architecture, MVVM, Firebase Auth, Firestore, FCM, and Hilt.

## Setup

1. Open `Jeeva-Bindu` in Android Studio.
2. Add `google-services.json` to `app/`.
3. Enable in Firebase Console:
   - Authentication -> Phone
   - Firestore Database
   - Cloud Messaging
4. Add SHA-1 and SHA-256 for your app.
5. Sync project and run.

## Firestore Collections

- `users`
- `emergency_alerts`
- `donor_responses`
- `notifications`

## Current Status

- Project scaffolded with 14 required screens and navigation.
- Clean architecture package structure is ready.
- Hilt and Firebase providers are configured.
- Placeholder repository implementations are included for next-step feature wiring.
