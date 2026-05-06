package com.juyel.totkaquiz.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.juyel.totkaquiz.data.GasApi
import com.juyel.totkaquiz.data.Prefs
import com.juyel.totkaquiz.data.TelegramApi
import com.juyel.totkaquiz.databinding.FragmentLoginBinding
import com.juyel.totkaquiz.home.HomeActivity
import com.juyel.totkaquiz.utils.*
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _b: FragmentLoginBinding? = null
    private val b get() = _b!!
    private val gasApi = GasApi()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?) =
        FragmentLoginBinding.inflate(inflater, container, false).also { _b = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.btnLogin.setOnClickListener { doLogin() }
        b.btnGoSignup.setOnClickListener {
            (activity as AuthActivity).goToSignupPage1()
        }
    }

    private fun doLogin() {
        val gmail = b.etGmail.text.toString().trim()
        val pass  = b.etPassword.text.toString().trim()

        if (gmail.isEmpty() || pass.isEmpty()) {
            b.root.snack("⚠️ Fill in all fields")
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            val result = gasApi.login(gmail, pass)
            setLoading(false)

            result.onSuccess { user ->
                val prefs = Prefs(requireContext())
                prefs.isLoggedIn  = true
                prefs.currentUser = user
                prefs.lastBoard   = user.board
                prefs.lastClass   = user.classVal

                // Telegram notification (direct from app)
                launch { TelegramApi.send(TelegramApi.loginMsg(user)) }

                requireContext().toast("স্বাগতম ${user.fullName}! 🎉")
                requireActivity().startActivityFinish<HomeActivity>()
            }
            result.onFailure {
                b.root.snack("❌ ${it.message ?: "Login failed"}")
            }
        }
    }

    private fun setLoading(on: Boolean) {
        b.btnLogin.isEnabled = !on
        b.progressBar.visibility = if (on) View.VISIBLE else View.GONE
        b.btnLogin.text = if (on) "Logging in…" else "Login"
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
