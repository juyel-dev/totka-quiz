package com.juyel.totka.data

import com.juyel.totka.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object TelegramApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    // ── Send message (fire-and-forget, won't crash app if fails) ─
    suspend fun send(text: String) = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("chat_id",    Constants.TG_CHAT_ID)
                put("text",       text)
                put("parse_mode", "Markdown")
            }
            val url = "${Constants.TG_API_BASE}${Constants.TG_BOT_TOKEN}/sendMessage"
            val req = Request.Builder()
                .url(url)
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()
            client.newCall(req).execute().close()
        } catch (_: Exception) {
            // Silent fail — Telegram is non-critical
        }
    }

    // ── Pre-built message templates ────────────────────────────
    suspend fun onSignup(name: String, gmail: String, board: String, classVal: String) {
        send("🆕 *New Signup!*\n👤 $name\n📧 `$gmail`\n🏫 *Board:* $board | *Class:* $classVal")
    }

    suspend fun onLogin(name: String, gmail: String) {
        send("🔑 *Login*\n👤 $name\n📧 `$gmail`")
    }

    suspend fun onProfileEdit(name: String, username: String, changes: List<String>) {
        val log = changes.joinToString("\n") { "• $it" }
        send("✏️ *Profile Updated*\n👤 $name (@${username.ifBlank { "no-username" }})\n\n$log")
    }
}
