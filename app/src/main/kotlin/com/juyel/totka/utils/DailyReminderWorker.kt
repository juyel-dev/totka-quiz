package com.juyel.totka.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.juyel.totka.R
import java.util.concurrent.TimeUnit

class DailyReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        if (!AppPrefs.isLoggedIn()) return Result.success()
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val nm = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel("quiz_remind", "Quiz Reminder",
                    NotificationManager.IMPORTANCE_DEFAULT)
            )
        }

        val note = NotificationCompat.Builder(applicationContext, "quiz_remind")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("📚 আজ কুইজ দিয়েছো?")
            .setContentText("প্রতিদিন কুইজ দিলে Streak বাড়ে! 🔥")
            .setAutoCancel(true)
            .build()

        nm.notify(1001, note)
    }

    companion object {
        fun schedule(ctx: Context) {
            val req = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
                "daily_quiz_reminder",
                ExistingPeriodicWorkPolicy.KEEP,
                req
            )
        }
    }
}
