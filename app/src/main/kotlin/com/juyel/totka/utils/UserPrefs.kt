package com.juyel.totka.utils

import android.content.Context
import android.content.SharedPreferences
import com.juyel.totka.data.models.User
import org.json.JSONArray
import org.json.JSONObject

class UserPrefs(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    // ── Auth ─────────────────────────────────────────────────
    val isLoggedIn: Boolean get() = prefs.getBoolean(Constants.KEY_LOGGED_IN, false)

    fun saveUser(user: User) {
        prefs.edit()
            .putBoolean(Constants.KEY_LOGGED_IN, true)
            .putString(Constants.KEY_USER_JSON, user.toJson().toString())
            .apply()
    }

    fun getUser(): User? {
        val json = prefs.getString(Constants.KEY_USER_JSON, null) ?: return null
        return try { User.fromJson(JSONObject(json)) } catch (e: Exception) { null }
    }

    fun updateUser(block: (User) -> User) {
        val current = getUser() ?: return
        saveUser(block(current))
    }

    fun logout() {
        prefs.edit()
            .putBoolean(Constants.KEY_LOGGED_IN, false)
            .remove(Constants.KEY_USER_JSON)
            .apply()
    }

    // ── Profile picture (local path) ─────────────────────────
    var profilePicPath: String?
        get() = prefs.getString(Constants.KEY_PROFILE_PIC, null)
        set(v) = prefs.edit().putString(Constants.KEY_PROFILE_PIC, v).apply()

    // ── Master sheet cache ────────────────────────────────────
    var masterCache: String?
        get() = prefs.getString(Constants.KEY_MASTER_CACHE, null)
        set(v) = prefs.edit().putString(Constants.KEY_MASTER_CACHE, v).apply()

    var masterTimestamp: Long
        get() = prefs.getLong(Constants.KEY_MASTER_TS, 0L)
        set(v) = prefs.edit().putLong(Constants.KEY_MASTER_TS, v).apply()

    val isMasterCacheValid: Boolean
        get() = masterCache != null &&
                (System.currentTimeMillis() - masterTimestamp) < Constants.MASTER_CACHE_TTL_MS

    // ── Streak ───────────────────────────────────────────────
    var streak: Int
        get() = prefs.getInt(Constants.KEY_STREAK, 0)
        set(v) = prefs.edit().putInt(Constants.KEY_STREAK, v).apply()

    var lastQuizDay: String
        get() = prefs.getString(Constants.KEY_LAST_QUIZ_DAY, "") ?: ""
        set(v) = prefs.edit().putString(Constants.KEY_LAST_QUIZ_DAY, v).apply()

    // ── Language ─────────────────────────────────────────────
    var language: String
        get() = prefs.getString(Constants.KEY_LANGUAGE, "bn") ?: "bn"
        set(v) = prefs.edit().putString(Constants.KEY_LANGUAGE, v).apply()
}
