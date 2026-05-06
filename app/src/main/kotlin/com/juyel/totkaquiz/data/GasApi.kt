package com.juyel.totkaquiz.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class GasApi {

    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
    private val gson = Gson()

    // ── Generic POST to GAS ───────────────────────────────────
    private suspend fun post(body: Map<String, Any?>): Result<JsonObject> =
        withContext(Dispatchers.IO) {
            try {
                val json    = gson.toJson(body)
                val reqBody = json.toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url(AppConfig.GAS_URL)
                    .post(reqBody)
                    .build()
                val response = client.newCall(request).execute()
                val text     = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
                val obj      = gson.fromJson(text, JsonObject::class.java)
                if (obj.get("ok")?.asBoolean == true) {
                    Result.success(obj.getAsJsonObject("data") ?: JsonObject())
                } else {
                    Result.failure(Exception(obj.get("error")?.asString ?: "GAS error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ── SIGNUP ────────────────────────────────────────────────
    suspend fun signup(
        gmail: String, password: String, fullName: String,
        username: String, bio: String, board: String,
        classVal: String, phone: String, favSubject: String,
        socialLinks: Map<String, String>
    ): Result<User> {
        val res = post(mapOf(
            "action"      to "signup",
            "gmail"       to gmail,
            "password"    to password,
            "fullName"    to fullName,
            "username"    to username,
            "bio"         to bio,
            "board"       to board,
            "classVal"    to classVal,
            "phone"       to phone,
            "favSubject"  to favSubject,
            "socialLinks" to socialLinks
        ))
        return res.map { gson.fromJson(it, User::class.java) }
    }

    // ── LOGIN ─────────────────────────────────────────────────
    suspend fun login(gmail: String, password: String): Result<User> {
        val res = post(mapOf(
            "action"   to "login",
            "gmail"    to gmail,
            "password" to password
        ))
        return res.map { gson.fromJson(it, User::class.java) }
    }

    // ── GET PROFILE ───────────────────────────────────────────
    suspend fun getProfile(userId: String): Result<User> {
        val res = post(mapOf("action" to "getProfile", "userId" to userId))
        return res.map { gson.fromJson(it, User::class.java) }
    }

    // ── UPDATE PROFILE ────────────────────────────────────────
    suspend fun updateProfile(userId: String, changes: Map<String, Any?>): Result<JsonObject> {
        return post(mapOf(
            "action"  to "updateProfile",
            "userId"  to userId,
            "changes" to changes
        ))
    }

    // ── GET MASTER (via GAS) ──────────────────────────────────
    suspend fun getMaster(): Result<List<MasterRow>> =
        withContext(Dispatchers.IO) {
            try {
                val request  = Request.Builder().url(AppConfig.GAS_URL + "?action=getMaster").get().build()
                val response = client.newCall(request).execute()
                val text     = response.body?.string() ?: return@withContext Result.failure(Exception("Empty"))
                val obj      = gson.fromJson(text, JsonObject::class.java)
                if (obj.get("ok")?.asBoolean != true)
                    return@withContext Result.failure(Exception(obj.get("error")?.asString))
                val arr  = obj.getAsJsonArray("data")
                val list = arr.map { gson.fromJson(it, MasterRow::class.java) }
                Result.success(list)
            } catch(e: Exception) {
                Result.failure(e)
            }
        }
}
