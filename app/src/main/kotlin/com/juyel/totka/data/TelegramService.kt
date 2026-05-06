package com.juyel.totka.data

import android.util.Log
import com.juyel.totka.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Sends messages DIRECTLY from app to Telegram Bot
 * (No GAS involved — just OkHttp → Telegram API)
 */
object TelegramService {

    private val client = OkHttpClient()
    private val JSON   = "application/json; charset=utf-8".toMediaType()
    private val TOKEN  = BuildConfig.TG_BOT_TOKEN
    private val CHAT   = BuildConfig.TG_CHAT_ID

    fun send(text: String) {
        if (TOKEN == "YOUR_BOT_TOKEN_HERE") {
            Log.w("TG", "Bot token not set — skipping Telegram message")
            return
        }
        val body = JSONObject().apply {
            put("chat_id", CHAT)
            put("text", text)
            put("parse_mode", "Markdown")
        }
        val req = Request.Builder()
            .url("https://api.telegram.org/bot$TOKEN/sendMessage")
            .post(body.toString().toRequestBody(JSON))
            .build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: okio.IOException) {
                Log.e("TG", "Send failed: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                Log.d("TG", "Sent: ${response.code}")
            }
        })
    }

    fun notifySignup(name: String, gmail: String, board: String, classVal: String) =
        send("🆕 *New Signup!*\n👤 $name\n📧 $gmail\n🏫 Board: $board | Class: $classVal")

    fun notifyLogin(name: String, gmail: String) =
        send("🔑 *Login:* $name ($gmail)")

    fun notifyProfileEdit(username: String, changes: List<String>) =
        send("✏️ *Profile Edit*\n@$username\n\n${changes.joinToString("\n")}")

    fun notifyQuizDone(name: String, subject: String, score: String) =
        send("📝 *Quiz Done!*\n👤 $name\n📚 $subject\n🎯 Score: $score")
}
