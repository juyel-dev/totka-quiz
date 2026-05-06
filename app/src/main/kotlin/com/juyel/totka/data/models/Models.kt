package com.juyel.totka.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val id:          String = "",
    val chapter:     String = "",
    val difficulty:  String = "",   // easy | medium | hard
    val question:    String = "",
    val optA:        String = "",
    val optB:        String = "",
    val optC:        String = "",
    val optD:        String = "",
    val correct:     String = "",   // A | B | C | D
    val explanation: String = ""
) : Parcelable {
    fun options(): List<String> = listOf(optA, optB, optC, optD)
    fun correctIndex(): Int = when (correct.uppercase()) {
        "A" -> 0; "B" -> 1; "C" -> 2; "D" -> 3; else -> 0
    }
}

@Parcelize
data class MasterEntry(
    val sheetId:   String = "",
    val sheetName: String = "",
    val board:     String = "",
    val classVal:  String = "",
    val subject:   String = "",
    val chapter:   String = "",
    val csvLink:   String = ""
) : Parcelable
