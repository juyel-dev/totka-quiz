package com.juyel.totkaquiz.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

// ── View helpers ──────────────────────────────────────────────
fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }

fun View.enable()  { isEnabled = true;  alpha = 1f }
fun View.disable() { isEnabled = false; alpha = 0.5f }

// ── Context helpers ───────────────────────────────────────────
fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Context.toastLong(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

fun View.snack(msg: String, duration: Int = Snackbar.LENGTH_SHORT) =
    Snackbar.make(this, msg, duration).show()

// ── Activity navigation ───────────────────────────────────────
inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java).apply(block)
    startActivity(intent)
}

inline fun <reified T : Activity> Activity.startActivityFinish(block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
    finish()
}

// ── Date ──────────────────────────────────────────────────────
fun todayString(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

fun Long.toTimeString(): String {
    val mins = this / 60
    val secs = this % 60
    return if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
}

// ── String helpers ────────────────────────────────────────────
fun String.capitalizeFirst() =
    if (isEmpty()) this else this[0].uppercaseChar() + substring(1)

fun String.isValidEmail() =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isValidPhone() =
    matches(Regex("^[+]?[0-9]{10,13}$"))
