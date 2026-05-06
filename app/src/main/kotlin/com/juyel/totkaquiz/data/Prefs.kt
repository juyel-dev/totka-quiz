package com.juyel.totkaquiz.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Prefs(context: Context) {

    private val sp: SharedPreferences =
        context.getSharedPreferences("totka_quiz_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // ── Auth ──────────────────────────────────────────────────
    var isLoggedIn: Boolean
        get() = sp.getBoolean("is_logged_in", false)
        set(v) = sp.edit().putBoolean("is_logged_in", v).apply()

    var currentUser: User?
        get() {
            val json = sp.getString("current_user", null) ?: return null
            return User.fromJson(json)
        }
        set(u) = sp.edit().putString("current_user", u?.toJson()).apply()

    // ── Profile Picture (local URI) ───────────────────────────
    var profilePicUri: String?
        get() = sp.getString("profile_pic_uri", null)
        set(v) = sp.edit().putString("profile_pic_uri", v).apply()

    // ── Quiz Settings ─────────────────────────────────────────
    var lastBoard: String
        get() = sp.getString("last_board", "") ?: ""
        set(v) = sp.edit().putString("last_board", v).apply()

    var lastClass: String
        get() = sp.getString("last_class", "") ?: ""
        set(v) = sp.edit().putString("last_class", v).apply()

    var lastSubject: String
        get() = sp.getString("last_subject", "") ?: ""
        set(v) = sp.edit().putString("last_subject", v).apply()

    var language: String
        get() = sp.getString("language", "bn") ?: "bn"
        set(v) = sp.edit().putString("language", v).apply()

    // ── Streak ────────────────────────────────────────────────
    var streakCount: Int
        get() = sp.getInt("streak_count", 0)
        set(v) = sp.edit().putInt("streak_count", v).apply()

    var lastQuizDate: String
        get() = sp.getString("last_quiz_date", "") ?: ""
        set(v) = sp.edit().putString("last_quiz_date", v).apply()

    // ── Bookmarks ─────────────────────────────────────────────
    var bookmarks: MutableList<String>
        get() {
            val json = sp.getString("bookmarks", "[]") ?: "[]"
            return try {
                val type = object : TypeToken<MutableList<String>>() {}.type
                gson.fromJson(json, type) ?: mutableListOf()
            } catch (e: Exception) { mutableListOf() }
        }
        set(list) = sp.edit().putString("bookmarks", gson.toJson(list)).apply()

    // ── Quiz History ──────────────────────────────────────────
    fun saveQuizResult(result: QuizResult) {
        val list = getQuizHistory().toMutableList()
        list.add(0, result)
        if (list.size > 50) list.removeLast()  // keep last 50
        sp.edit().putString("quiz_history", gson.toJson(list)).apply()
    }

    fun getQuizHistory(): List<QuizResult> {
        val json = sp.getString("quiz_history", "[]") ?: "[]"
        return try {
            val type = object : TypeToken<List<QuizResult>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch(e: Exception) { emptyList() }
    }

    // ── Master Cache ──────────────────────────────────────────
    var masterCache: String?
        get() = sp.getString("master_cache", null)
        set(v) = sp.edit().putString("master_cache", v).apply()

    var masterCacheTs: Long
        get() = sp.getLong("master_cache_ts", 0L)
        set(v) = sp.edit().putLong("master_cache_ts", v).apply()

    // ── Misc ──────────────────────────────────────────────────
    fun clear() = sp.edit().clear().apply()
}
