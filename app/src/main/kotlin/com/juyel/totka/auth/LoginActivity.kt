package com.juyel.totka.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.juyel.totka.data.GasApi
import com.juyel.totka.data.TelegramApi
import com.juyel.totka.databinding.ActivityLoginBinding
import com.juyel.totka.home.HomeActivity
import com.juyel.totka.utils.UserPrefs
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: UserPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = UserPrefs(this)

        binding.btnLogin.setOnClickListener { doLogin() }
        binding.txtGoSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun doLogin() {
        val gmail    = binding.etGmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (gmail.isBlank() || password.isBlank()) {
            snack("Gmail/Phone আর Password দিন"); return
        }

        setLoading(true)
        lifecycleScope.launch {
            val result = GasApi.login(gmail, password)
            setLoading(false)
            result.fold(
                onSuccess = { user ->
                    prefs.saveUser(user)
                    TelegramApi.onLogin(user.fullName, user.gmail)
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finishAffinity()
                },
                onFailure = { e -> snack(e.message ?: "Login failed") }
            )
        }
    }

    private fun setLoading(b: Boolean) {
        binding.btnLogin.isEnabled  = !b
        binding.progressBar.visibility = if (b) View.VISIBLE else View.GONE
    }

    private fun snack(msg: String) =
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
}
