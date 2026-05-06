package com.juyel.totkaquiz.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object TelegramApi {

    private val client = OkHttpClient()

    /** Send message directly from app to Telegram bot (no GAS) */
    suspend fun send(text: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val body = JSONObject().apply {
                put("chat_id",    AppConfig.TG_CHAT_ID)
                put("text",       text)
                put("parse_mode", "Markdown")
            }.toString()

            val request = Request.Builder()
                .url(AppConfig.TG_API_URL)
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()

            val resp = client.newCall(request).execute()
            resp.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // ── Message Builders ──────────────────────────────────────

    fun signupMsg(user: User): String = """
🆕 *New Signup!*
👤 ${user.fullName}  (@${user.username.ifEmpty{"—"}})
📧 ${user.gmail}
🏫 Board: ${user.board.ifEmpty{"—"}}  |  Class: ${user.classVal.ifEmpty{"—"}}
📚 Fav: ${user.favSubject.ifEmpty{"—"}}
🕐 Just now
    """.trimIndent()

    fun loginMsg(user: User): String = """
🔑 *Login*
👤 ${user.fullName}  (${user.gmail})
    """.trimIndent()

    fun profileEditMsg(user: User, changeLog: List<String>): String = """
✏️ *Profile Updated*
👤 ${user.fullName}  (@${user.username.ifEmpty{"—"}})

${changeLog.joinToString("\n")}
    """.trimIndent()

    fun quizResultMsg(user: User, result: QuizResult): String = """
📊 *Quiz Completed*
👤 ${user.fullName}
📚 ${result.subject}  |  ${result.chapter}
✅ ${result.correct} / ${result.totalQuestions}  (${result.percentage}%)
⏱️ ${result.timeSecs}s
    """.trimIndent()
}
