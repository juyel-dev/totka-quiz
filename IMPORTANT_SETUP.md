# ⚡ Totka Quiz — Setup Guide

## After unzipping Part 1 + Part 2

### Folder structure to merge:
```
totka-quiz/              ← Part 1 ZIP
  + app/src/main/kotlin/com/juyel/totka/home/
  + app/src/main/kotlin/com/juyel/totka/quiz/
  + app/src/main/kotlin/com/juyel/totka/result/
  + app/src/main/kotlin/com/juyel/totka/profile/
  + app/src/main/kotlin/com/juyel/totka/data/db/
  + app/src/main/kotlin/com/juyel/totka/utils/StreakHelper.kt
  + app/src/main/kotlin/com/juyel/totka/utils/AppPrefsExt.kt
  + app/src/main/res/layout/activity_home.xml
  + app/src/main/res/layout/activity_quiz.xml
  + app/src/main/res/layout/activity_result.xml
  + app/src/main/res/layout/activity_profile.xml
  + app/src/main/res/layout/activity_edit_profile.xml
  + app/src/main/res/raw/confetti.json
```

## 3 জায়গায় Replace করো:
1. `app/build.gradle.kts` → `YOUR_BOT_TOKEN_HERE` → real Telegram token
2. GAS এ `CONFIG.TG_TOKEN` → real token
3. Master Sheet Row 1 header → `sheetId,sheetName,board,class,subject,chapter,csvLink`

## GitHub এ push করো (Termux):
```bash
cd totka-quiz
git init
git add .
git commit -m "Initial Kotlin Android app"
git remote add origin https://github.com/juyel-dev/totka-quiz.git
git push -u origin main
```

## APK নামানো:
GitHub → Actions → Build APK → Artifacts → Download ZIP → APK বের করো

## Confetti sound:
`app/src/main/res/raw/clap.mp3` — নিজে একটা clap.mp3 add করো
(Free: pixabay.com/sound-effects → "clapping")

## Lottie confetti animation:
`app/src/main/res/raw/confetti.json` — placeholder আছে
Real one: lottiefiles.com → search "confetti" → download JSON → replace

---
Made with ❤️ for juyel-dev
