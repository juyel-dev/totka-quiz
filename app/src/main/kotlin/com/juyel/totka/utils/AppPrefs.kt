package com.juyel.totka.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.juyel.totka.data.model.UserProfile

object AppPrefs {
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    private const val KEY_USER    = "user_profile"
    private const val KEY_LOGGED  = "is_logged_in"
    private const val KEY_LANG    = "app_language"
    private const val KEY_THEME   = "app_theme"
    private const val KEY_STREAK  = "daily_streak"
    private const val KEY_LAST_Q  = "last_quiz_date"

    fun init(ctx: Context) {
        prefs = ctx.getSharedPreferences("totka_prefs", Context.MODE_PRIVATE)
    }

    // ── Auth ─────────────────────────────────────────────────
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED, false)

    fun saveUser(user: UserProfile) {
        prefs.edit()
            .putString(KEY_USER, gson.toJson(user))
            .putBoolean(KEY_LOGGED, true)
            .apply()
    }

    fun getUser(): UserProfile? = try {
        gson.fromJson(prefs.getString(KEY_USER, null), UserProfile::class.java)
    } catch (e: Exception) { null }

    fun logout() {
        prefs.edit()
            .remove(KEY_USER)
            .putBoolean(KEY_LOGGED, false)
            .apply()
    }

    // ── Settings ─────────────────────────────────────────────
    var language: String
        get()      = prefs.getString(KEY_LANG, "bn") ?: "bn"
        set(value) = prefs.edit().putString(KEY_LANG, value).apply()

    var isDarkTheme: Boolean
        get()      = prefs.getBoolean(KEY_THEME, true)
        set(value) = prefs.edit().putBoolean(KEY_THEME, value).apply()

    // ── Streak ───────────────────────────────────────────────
    var streak: Int
        get()      = prefs.getInt(KEY_STREAK, 0)
        set(value) = prefs.edit().putInt(KEY_STREAK, value).apply()

    var lastQuizDate: String
        get()      = prefs.getString(KEY_LAST_Q, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_Q, value).apply()
}
