package com.juyel.totka

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.juyel.totka.auth.LoginActivity
import com.juyel.totka.home.HomeActivity
import com.juyel.totka.utils.UserPrefs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = UserPrefs(this)

        lifecycleScope.launch {
            delay(1800)   // Show splash for 1.8 s
            val dest = if (prefs.isLoggedIn) HomeActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this@MainActivity, dest))
            finish()
        }
    }
}
