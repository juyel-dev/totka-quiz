package com.juyel.totka.utils

import android.content.SharedPreferences
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.juyel.totka.data.model.MasterRow

fun AppPrefs.getAvatarUri(): Uri? {
    val str = rawPrefs().getString("avatar_uri", null) ?: return null
    return Uri.parse(str)
}

fun AppPrefs.cacheMaster(rows: List<MasterRow>) {
    rawPrefs().edit().putString("master_cache", Gson().toJson(rows)).apply()
}

fun AppPrefs.getCachedMaster(): List<MasterRow>? {
    val json = rawPrefs().getString("master_cache", null) ?: return null
    return try {
        Gson().fromJson(json, object : TypeToken<List<MasterRow>>() {}.type)
    } catch (e: Exception) { null }
}

// Access private prefs field via reflection for extension fns
private fun AppPrefs.rawPrefs(): SharedPreferences {
    val f = AppPrefs::class.java.getDeclaredField("prefs")
    f.isAccessible = true
    return f.get(this) as SharedPreferences
}
