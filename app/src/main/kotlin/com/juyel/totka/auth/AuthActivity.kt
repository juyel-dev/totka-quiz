package com.juyel.totka.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.juyel.totka.R
import com.juyel.totka.data.ApiService
import com.juyel.totka.data.TelegramService
import com.juyel.totka.home.HomeActivity
import com.juyel.totka.utils.AppPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthActivity : AppCompatActivity() {

    private lateinit var etGmail:    EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin:   Button
    private lateinit var btnGoSignup: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        etGmail    = findViewById(R.id.et_gmail)
        etPassword = findViewById(R.id.et_password)
        btnLogin   = findViewById(R.id.btn_login)
        btnGoSignup = findViewById(R.id.btn_go_signup)
        progressBar = findViewById(R.id.progress_bar)

        btnLogin.setOnClickListener { doLogin() }
        btnGoSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun doLogin() {
        val gmail    = etGmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (gmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.err_fill_all), Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        lifecycleScope.launch(Dispatchers.IO) {
            val result = suspendCancellableCoroutine { cont ->
                ApiService.login(gmail, password) { user, err -> cont.resume(Pair(user, err)) }
            }
            launch(Dispatchers.Main) {
                setLoading(false)
                val (user, err) = result
                if (user != null) {
                    AppPrefs.saveUser(user)
                    TelegramService.notifyLogin(user.fullName, user.gmail)
                    startActivity(Intent(this@AuthActivity, HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    finish()
                } else {
                    Toast.makeText(this@AuthActivity, err ?: "Login failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnLogin.isEnabled     = !loading
    }
}
