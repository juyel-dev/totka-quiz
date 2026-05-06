package com.juyel.totka.data

import com.juyel.totka.data.models.User
import com.juyel.totka.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GasApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    // ── Signup ───────────────────────────────────────────────
    suspend fun signup(
        gmail: String, password: String, fullName: String,
        username: String, bio: String, board: String,
        classVal: String, phone: String, favSubject: String,
        socialLinks: JSONObject
    ): Result<User> = postJson(JSONObject().apply {
        put("action",      "signup")
        put("gmail",       gmail)
        put("password",    password)
        put("fullName",    fullName)
        put("username",    username)
        put("bio",         bio)
        put("board",       board)
        put("classVal",    classVal)
        put("phone",       phone)
        put("favSubject",  favSubject)
        put("socialLinks", socialLinks)
    }) { User.fromJson(it) }

    // ── Login ─────────────────────────────────────────────────
    suspend fun login(gmail: String, password: String): Result<User> =
        postJson(JSONObject().apply {
            put("action",   "login")
            put("gmail",    gmail)
            put("password", password)
        }) { User.fromJson(it) }

    // ── Update Profile ────────────────────────────────────────
    suspend fun updateProfile(userId: String, changes: JSONObject): Result<String> =
        postJson(JSONObject().apply {
            put("action",  "updateProfile")
            put("userId",  userId)
            put("changes", changes)
        }) { it.optString("updated", "0") + " fields updated" }

    // ── Fetch Master (via GAS) ────────────────────────────────
    suspend fun getMaster(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val req = Request.Builder().url("${Constants.GAS_URL}?action=getMaster").get().build()
            val response = client.newCall(req).execute()
            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val json = JSONObject(body)
            if (!json.optBoolean("ok", false)) {
                return@withContext Result.failure(Exception(json.optString("error", "GAS error")))
            }
            Result.success(json.getJSONArray("data").toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Fetch CSV directly (questions / master) ───────────────
    suspend fun fetchCsv(url: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val cacheBusted = if (url.contains("?")) "$url&_t=${System.currentTimeMillis()}"
                              else "$url?_t=${System.currentTimeMillis()}"
            val req = Request.Builder().url(cacheBusted).get().build()
            val response = client.newCall(req).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}"))
            }
            Result.success(response.body?.string() ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Internal: POST JSON to GAS ────────────────────────────
    private suspend fun <T> postJson(
        payload: JSONObject,
        mapper: (JSONObject) -> T
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            val body = payload.toString().toRequestBody("application/json".toMediaType())
            val req  = Request.Builder().url(Constants.GAS_URL).post(body).build()
            val res  = client.newCall(req).execute()
            val raw  = res.body?.string() ?: return@withContext Result.failure(Exception("Empty"))
            val json = JSONObject(raw)
            if (!json.optBoolean("ok", false)) {
                return@withContext Result.failure(Exception(json.optString("error", "Unknown error")))
            }
            Result.success(mapper(json.getJSONObject("data")))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
