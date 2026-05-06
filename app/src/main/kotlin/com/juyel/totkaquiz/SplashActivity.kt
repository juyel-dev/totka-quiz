package com.juyel.totkaquiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.juyel.totkaquiz.auth.AuthActivity
import com.juyel.totkaquiz.data.Prefs
import com.juyel.totkaquiz.home.HomeActivity
import com.juyel.totkaquiz.utils.startActivityFinish
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(1800)
            val prefs = Prefs(this@SplashActivity)
            if (prefs.isLoggedIn && prefs.currentUser != null) {
                startActivityFinish<HomeActivity>()
            } else {
                startActivityFinish<AuthActivity>()
            }
        }
    }
}
