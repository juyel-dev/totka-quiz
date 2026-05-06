package com.juyel.totka

import android.app.Application
import com.juyel.totka.utils.AppPrefs
import com.juyel.totka.utils.DailyReminderWorker

class TotkaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppPrefs.init(this)
        DailyReminderWorker.schedule(this)
    }
}
