package com.juyel.totka.data

import com.juyel.totka.data.models.MasterEntry
import com.juyel.totka.data.models.Question

object CsvParser {

    // ── Master CSV ────────────────────────────────────────────
    // Columns: sheetId, sheetName, board, class, subject, chapter, csvLink
    fun parseMaster(csv: String): List<MasterEntry> {
        val lines = csv.trim().lines()
        if (lines.size < 2) return emptyList()
        return lines.drop(1)            // skip header
            .mapNotNull { line ->
                val cols = splitCsvLine(line)
                if (cols.size < 7) return@mapNotNull null
                val link = cols[6].trim()
                if (link.isBlank()) return@mapNotNull null
                MasterEntry(
                    sheetId   = cols[0].trim(),
                    sheetName = cols[1].trim(),
                    board     = cols[2].trim(),
                    classVal  = cols[3].trim(),
                    subject   = cols[4].trim(),
                    chapter   = cols[5].trim(),
                    csvLink   = link
                )
            }
    }

    // ── Question CSV ─────────────────────────────────────────
    // Columns: id, chapter, difficulty, question, optA, optB, optC, optD, correct, explanation
    fun parseQuestions(csv: String): List<Question> {
        val lines = csv.trim().lines()
        if (lines.size < 2) return emptyList()
        return lines.drop(1)
            .mapNotNull { line ->
                val cols = splitCsvLine(line)
                if (cols.size < 9) return@mapNotNull null
                Question(
                    id          = cols.getOrElse(0) { "" }.trim(),
                    chapter     = cols.getOrElse(1) { "" }.trim(),
                    difficulty  = cols.getOrElse(2) { "" }.trim().lowercase(),
                    question    = cols.getOrElse(3) { "" }.trim(),
                    optA        = cols.getOrElse(4) { "" }.trim(),
                    optB        = cols.getOrElse(5) { "" }.trim(),
                    optC        = cols.getOrElse(6) { "" }.trim(),
                    optD        = cols.getOrElse(7) { "" }.trim(),
                    correct     = cols.getOrElse(8) { "A" }.trim().uppercase(),
                    explanation = cols.getOrElse(9) { "" }.trim()
                )
            }
            .filter { it.question.isNotBlank() }
    }

    // ── Simple CSV line splitter (handles quoted fields) ──────
    private fun splitCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '"' && !inQuotes -> inQuotes = true
                c == '"' && inQuotes  -> {
                    if (i + 1 < line.length && line[i + 1] == '"') {
                        sb.append('"'); i++   // escaped quote
                    } else inQuotes = false
                }
                c == ',' && !inQuotes -> { result.add(sb.toString()); sb.clear() }
                else -> sb.append(c)
            }
            i++
        }
        result.add(sb.toString())
        return result
    }
}
