package com.juyel.totka

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.juyel.totka.auth.AuthActivity
import com.juyel.totka.home.HomeActivity
import com.juyel.totka.utils.AppPrefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(1800L)
            val dest = if (AppPrefs.isLoggedIn())
                HomeActivity::class.java
            else
                AuthActivity::class.java
            startActivity(Intent(this@SplashActivity, dest))
            finish()
        }
    }
}
