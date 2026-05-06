package com.juyel.totka.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.juyel.totka.BuildConfig
import com.juyel.totka.data.model.SocialLinks
import com.juyel.totka.data.model.UserProfile
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object ApiService {

    private val client = OkHttpClient()
    private val gson   = Gson()
    private val JSON   = "application/json; charset=utf-8".toMediaType()

    // ── Generic POST to GAS ──────────────────────────────────
    private fun gasPost(body: JSONObject, onResult: (JSONObject?) -> Unit) {
        val req = Request.Builder()
            .url(BuildConfig.GAS_URL)
            .post(body.toString().toRequestBody(JSON))
            .build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { onResult(null) }
            override fun onResponse(call: Call, response: Response) {
                val raw = response.body?.string()
                onResult(raw?.let { runCatching { JSONObject(it) }.getOrNull() })
            }
        })
    }

    // ── SIGNUP ───────────────────────────────────────────────
    fun signup(
        gmail: String, password: String, fullName: String,
        username: String, bio: String, board: String, classVal: String,
        phone: String, favSubject: String, socialLinks: SocialLinks,
        onResult: (UserProfile?) -> Unit
    ) {
        val body = JSONObject().apply {
            put("action", "signup")
            put("gmail", gmail); put("password", password)
            put("fullName", fullName); put("username", username)
            put("bio", bio); put("board", board); put("classVal", classVal)
            put("phone", phone); put("favSubject", favSubject)
            put("socialLinks", JSONObject(gson.toJson(socialLinks)))
        }
        gasPost(body) { res ->
            if (res != null && res.optBoolean("ok")) {
                val d = res.getJSONObject("data")
                onResult(UserProfile(
                    userId   = d.optString("userId"),
                    gmail    = d.optString("gmail"),
                    fullName = d.optString("fullName"),
                    username = d.optString("username"),
                    board    = d.optString("board"),
                    classVal = d.optString("classVal"),
                ))
            } else onResult(null)
        }
    }

    // ── LOGIN ────────────────────────────────────────────────
    fun login(
        gmail: String, password: String,
        onResult: (UserProfile?, String?) -> Unit
    ) {
        val body = JSONObject().apply {
            put("action", "login")
            put("gmail", gmail)
            put("password", password)
        }
        gasPost(body) { res ->
            if (res == null) { onResult(null, "Network error"); return@gasPost }
            if (!res.optBoolean("ok")) {
                onResult(null, res.optString("error", "Login failed"))
                return@gasPost
            }
            val d = res.getJSONObject("data")
            val social = runCatching {
                gson.fromJson(d.optString("socialLinks","{}"), SocialLinks::class.java)
            }.getOrDefault(SocialLinks())
            onResult(
                UserProfile(
                    userId     = d.optString("userId"),
                    gmail      = d.optString("gmail"),
                    fullName   = d.optString("fullName"),
                    username   = d.optString("username"),
                    bio        = d.optString("bio"),
                    board      = d.optString("board"),
                    classVal   = d.optString("classVal"),
                    phone      = d.optString("phone"),
                    favSubject = d.optString("favSubject"),
                    socialLinks = social,
                    joinDate   = d.optString("joinDate"),
                ), null
            )
        }
    }

    // ── UPDATE PROFILE ───────────────────────────────────────
    fun updateProfile(
        userId: String,
        changes: Map<String, Any>,
        onResult: (Boolean) -> Unit
    ) {
        val body = JSONObject().apply {
            put("action", "updateProfile")
            put("userId", userId)
            put("changes", JSONObject(changes))
        }
        gasPost(body) { res -> onResult(res?.optBoolean("ok") == true) }
    }

    // ── GET MASTER CSV ───────────────────────────────────────
    fun fetchMasterCsv(onResult: (String?) -> Unit) {
        val req = Request.Builder().url(BuildConfig.MASTER_CSV).get().build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { onResult(null) }
            override fun onResponse(call: Call, response: Response) {
                onResult(response.body?.string())
            }
        })
    }

    // ── FETCH CHAPTER CSV ────────────────────────────────────
    fun fetchChapterCsv(url: String, onResult: (String?) -> Unit) {
        val cacheBusted = "$url&t=${System.currentTimeMillis()}"
        val req = Request.Builder().url(cacheBusted).get().build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { onResult(null) }
            override fun onResponse(call: Call, response: Response) {
                onResult(response.body?.string())
            }
        })
    }
}
