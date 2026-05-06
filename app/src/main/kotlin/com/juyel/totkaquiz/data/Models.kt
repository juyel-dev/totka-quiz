package com.juyel.totkaquiz.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// ── User ─────────────────────────────────────────────────────
data class User(
    val userId:      String = "",
    val gmail:       String = "",
    val fullName:    String = "",
    val username:    String = "",
    val bio:         String = "",
    val board:       String = "",
    val classVal:    String = "",
    val phone:       String = "",
    val favSubject:  String = "",
    val socialLinks: Map<String, String> = emptyMap(),
    val joinDate:    String = ""
) {
    fun toJson(): String = Gson().toJson(this)
    companion object {
        fun fromJson(json: String): User? = try {
            Gson().fromJson(json, User::class.java)
        } catch(e: Exception) { null }
    }
}

// ── Master Row ────────────────────────────────────────────────
data class MasterRow(
    val sheetId:   String = "",
    val sheetName: String = "",
    val board:     String = "",
    val classVal:  String = "",
    val subject:   String = "",
    val chapter:   String = "",
    val csvLink:   String = ""
)

// ── Question ─────────────────────────────────────────────────
data class Question(
    val id:          String = "",
    val chapter:     String = "",
    val difficulty:  String = "easy",   // easy / medium / hard
    val question:    String = "",
    val optA:        String = "",
    val optB:        String = "",
    val optC:        String = "",
    val optD:        String = "",
    val correct:     String = "",       // A / B / C / D
    val explanation: String = "",
    var userAnswer:  String = "",       // set during quiz
    var bookmarked:  Boolean = false
) {
    val isCorrect  get() = userAnswer.equals(correct, ignoreCase = true)
    val isAnswered get() = userAnswer.isNotEmpty()
    val options    get() = listOf(optA, optB, optC, optD)
}

// ── Quiz Result ───────────────────────────────────────────────
data class QuizResult(
    val totalQuestions: Int,
    val correct:        Int,
    val wrong:          Int,
    val skipped:        Int,
    val timeSecs:       Long,
    val subject:        String,
    val chapter:        String,
    val board:          String,
    val classVal:       String,
    val timestamp:      Long = System.currentTimeMillis()
) {
    val percentage get() = if (totalQuestions == 0) 0 else (correct * 100) / totalQuestions
}
