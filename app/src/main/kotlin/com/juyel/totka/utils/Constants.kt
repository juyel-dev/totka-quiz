package com.juyel.totka.utils

object Constants {
    // ── GAS ─────────────────────────────────────────────────
    const val GAS_URL =
        "https://script.google.com/macros/s/AKfycbzrSAsUNxfsW2mtV7yQrOiPB6IloQ2XhMAgVhOmFXsQXk8KZSvD2UXYra-LnB0Id8vo1Q/exec"

    // ── Sheets ───────────────────────────────────────────────
    const val MASTER_CSV_URL =
        "https://docs.google.com/spreadsheets/d/1dJtuu61H_i1q_xL4--b4xsFtCVxzIi301bIDIAz1qdw/export?format=csv"

    // ── Telegram ─────────────────────────────────────────────
    const val TG_BOT_TOKEN  = "YOUR_BOT_TOKEN_HERE"   // ← Replace with real token
    const val TG_CHAT_ID    = "7929275539"
    const val TG_API_BASE   = "https://api.telegram.org/bot"

    // ── SharedPrefs keys ─────────────────────────────────────
    const val PREFS_NAME        = "totka_prefs"
    const val KEY_USER_JSON     = "user_json"
    const val KEY_LOGGED_IN     = "logged_in"
    const val KEY_PROFILE_PIC   = "profile_pic_path"
    const val KEY_MASTER_CACHE  = "master_cache"
    const val KEY_MASTER_TS     = "master_ts"
    const val KEY_STREAK        = "daily_streak"
    const val KEY_LAST_QUIZ_DAY = "last_quiz_day"
    const val KEY_LANGUAGE      = "app_language"

    // ── Intent extras ────────────────────────────────────────
    const val EXTRA_CSV_LINK    = "csv_link"
    const val EXTRA_BOARD       = "board"
    const val EXTRA_CLASS       = "class_val"
    const val EXTRA_SUBJECT     = "subject"
    const val EXTRA_CHAPTER     = "chapter"
    const val EXTRA_DIFFICULTY  = "difficulty"
    const val EXTRA_Q_COUNT     = "q_count"
    const val EXTRA_MODE        = "mode"       // "mcq" | "flashcard"

    // ── Misc ─────────────────────────────────────────────────
    const val MASTER_CACHE_TTL_MS = 5 * 60 * 1000L   // 5 minutes
    const val CONFETTI_THRESHOLD  = 70                 // %
}
