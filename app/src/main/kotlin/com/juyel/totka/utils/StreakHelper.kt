package com.juyel.totka.utils

import java.text.SimpleDateFormat
import java.util.*

object StreakHelper {
    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun updateStreak() {
        val today     = fmt.format(Date())
        val lastDate  = AppPrefs.lastQuizDate
        val yesterday = fmt.format(Date(System.currentTimeMillis() - 86_400_000L))
        AppPrefs.streak = when (lastDate) {
            today     -> AppPrefs.streak
            yesterday -> AppPrefs.streak + 1
            else      -> 1
        }
        AppPrefs.lastQuizDate = today
    }
}
