package com.juyel.totka.utils

import com.juyel.totka.data.model.MasterRow
import com.juyel.totka.data.model.Question

object CsvParser {

    // ── Parse Master Sheet CSV ────────────────────────────────
    // Header: sheetId,sheetName,board,class,subject,chapter,csvLink
    fun parseMaster(csv: String): List<MasterRow> {
        val lines = csv.trim().split("\n").drop(1)   // skip header
        return lines.mapNotNull { line ->
            val cols = splitCsvLine(line)
            if (cols.size < 7) return@mapNotNull null
            MasterRow(
                sheetid   = cols[0].trim(),
                sheetname = cols[1].trim(),
                board     = cols[2].trim(),
                classVal  = cols[3].trim(),
                subject   = cols[4].trim(),
                chapter   = cols[5].trim(),
                csvlink   = cols[6].trim(),
            )
        }
    }

    // ── Parse Chapter CSV ─────────────────────────────────────
    // Header: id,chapter,difficulty,question,optA,optB,optC,optD,correct,explanation
    fun parseChapter(csv: String): List<Question> {
        val lines = csv.trim().split("\n").drop(1)
        return lines.mapNotNull { line ->
            val cols = splitCsvLine(line)
            if (cols.size < 9) return@mapNotNull null
            Question(
                id          = cols[0].trim(),
                chapter     = cols[1].trim(),
                difficulty  = cols[2].trim().lowercase(),
                question    = cols[3].trim(),
                optA        = cols[4].trim(),
                optB        = cols[5].trim(),
                optC        = cols[6].trim(),
                optD        = cols[7].trim(),
                correct     = cols[8].trim().uppercase(),
                explanation = if (cols.size > 9) cols[9].trim() else "",
            )
        }
    }

    // Handle quoted fields with commas inside
    private fun splitCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var field  = StringBuilder()
        var inQuote = false
        for (ch in line) {
            when {
                ch == '"'            -> inQuote = !inQuote
                ch == ',' && !inQuote -> { result.add(field.toString()); field = StringBuilder() }
                else                 -> field.append(ch)
            }
        }
        result.add(field.toString())
        return result
    }
}
