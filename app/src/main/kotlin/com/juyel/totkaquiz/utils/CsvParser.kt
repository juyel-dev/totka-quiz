package com.juyel.totkaquiz.utils

import com.juyel.totkaquiz.data.MasterRow
import com.juyel.totkaquiz.data.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object CsvParser {

    private val client = OkHttpClient.Builder()
        .followRedirects(true).followSslRedirects(true).build()

    // ── Fetch CSV text from URL ───────────────────────────────
    suspend fun fetchCsv(url: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val req  = Request.Builder().url(url + "?t=" + System.currentTimeMillis()).get().build()
            val resp = client.newCall(req).execute()
            val text = resp.body?.string() ?: return@withContext Result.failure(Exception("Empty CSV"))
            Result.success(text)
        } catch(e: Exception) {
            Result.failure(e)
        }
    }

    // ── Parse Master CSV ──────────────────────────────────────
    // Expected headers: sheetId,sheetName,board,class,subject,chapter,csvLink
    fun parseMaster(csv: String): List<MasterRow> {
        val lines = csv.trim().split("\n").filter { it.isNotBlank() }
        if (lines.size < 2) return emptyList()
        val headers = parseLine(lines[0]).map { it.lowercase().trim().replace(" ","") }

        return lines.drop(1).mapNotNull { line ->
            val cols = parseLine(line)
            if (cols.size < 6) return@mapNotNull null
            fun col(name: String) = cols.getOrElse(headers.indexOf(name)) { "" }.trim()
            MasterRow(
                sheetId   = col("sheetid"),
                sheetName = col("sheetname"),
                board     = col("board"),
                classVal  = col("class"),
                subject   = col("subject"),
                chapter   = col("chapter"),
                csvLink   = col("csvlink")
            )
        }.filter { it.csvLink.isNotEmpty() || it.sheetId.isNotEmpty() }
    }

    // ── Parse Chapter CSV ─────────────────────────────────────
    // Expected: id,chapter,difficulty,question,optA,optB,optC,optD,correct,explanation
    fun parseQuestions(csv: String): List<Question> {
        val lines = csv.trim().split("\n").filter { it.isNotBlank() }
        if (lines.size < 2) return emptyList()
        val headers = parseLine(lines[0]).map { it.lowercase().trim() }

        return lines.drop(1).mapNotNull { line ->
            val cols = parseLine(line)
            if (cols.size < 9) return@mapNotNull null
            fun col(name: String) = cols.getOrElse(headers.indexOf(name)) { "" }.trim()
            Question(
                id          = col("id"),
                chapter     = col("chapter"),
                difficulty  = col("difficulty").ifEmpty { "easy" },
                question    = col("question"),
                optA        = col("opta"),
                optB        = col("optb"),
                optC        = col("optc"),
                optD        = col("optd"),
                correct     = col("correct"),
                explanation = col("explanation")
            )
        }.filter { it.question.isNotEmpty() }
    }

    // ── CSV line parser (handles quoted commas) ───────────────
    private fun parseLine(line: String): List<String> {
        val result  = mutableListOf<String>()
        val current = StringBuilder()
        var inQuote = false
        for (ch in line) {
            when {
                ch == '"'         -> inQuote = !inQuote
                ch == ',' && !inQuote -> { result.add(current.toString()); current.clear() }
                else              -> current.append(ch)
            }
        }
        result.add(current.toString())
        return result
    }
}
