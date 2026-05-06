# ⚡ Totka Quiz — Android App

বাংলাদেশের যেকোনো Board, Class, Subject এর জন্য Quiz App।

## Build করো
GitHub Actions → push করো → Artifacts থেকে APK নামাও।

## Setup
1. `app/build.gradle.kts` এ `TG_BOT_TOKEN` replace করো
2. GAS deploy করো → URL already set আছে
3. Master Sheet এ board column যোগ করো

## Tech Stack
- Kotlin + Android SDK
- OkHttp (API calls)
- Room DB (local storage)
- Glide (image loading)
- WorkManager (notifications)

## Screens
- Splash → Auto login check
- Auth (Login / 3-page Signup)
- Home (Board/Class/Subject/Chapter select)
- Quiz (MCQ + Timer)
- Result (Score + Confetti + Share)
- Profile (View + Edit → Telegram alert)
