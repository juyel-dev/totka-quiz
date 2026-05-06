package com.juyel.totkaquiz.auth

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.juyel.totkaquiz.data.*
import com.juyel.totkaquiz.databinding.FragmentSignupP3Binding
import com.juyel.totkaquiz.home.HomeActivity
import com.juyel.totkaquiz.utils.*
import kotlinx.coroutines.launch

class SignupPage3Fragment : Fragment() {
    private var _b: FragmentSignupP3Binding? = null
    private val b get() = _b!!
    private val gasApi = GasApi()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentSignupP3Binding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        val auth = activity as AuthActivity
        b.tvStep.text = "Step 3 / 3 — Academic Details"

        // Board Spinner
        val boardAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, AppConfig.BOARDS)
        b.spinnerBoard.adapter = boardAdapter

        // Class Spinner
        val classAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, AppConfig.CLASSES)
        b.spinnerClass.adapter = classAdapter

        b.btnSubmit.setOnClickListener {
            val username = b.etUsername.text.toString().trim()
            val bio      = b.etBio.text.toString().trim()
            val board    = b.spinnerBoard.selectedItem.toString()
            val classVal = b.spinnerClass.selectedItem.toString()
            val favSub   = b.etFavSubject.text.toString().trim()

            val socials = mutableMapOf<String, String>()
            if (b.etFacebook.text.toString().isNotBlank())
                socials["facebook"] = b.etFacebook.text.toString().trim()
            if (b.etInstagram.text.toString().isNotBlank())
                socials["instagram"] = b.etInstagram.text.toString().trim()
            if (b.etYoutube.text.toString().isNotBlank())
                socials["youtube"] = b.etYoutube.text.toString().trim()
            if (b.etTelegram.text.toString().isNotBlank())
                socials["telegram"] = b.etTelegram.text.toString().trim()

            if (username.isEmpty()) { b.root.snack("Enter a username"); return@setOnClickListener }

            setLoading(true)

            lifecycleScope.launch {
                val result = gasApi.signup(
                    gmail       = auth.signupGmail,
                    password    = auth.signupPassword,
                    fullName    = auth.signupName,
                    username    = username,
                    bio         = bio,
                    board       = board,
                    classVal    = classVal,
                    phone       = if (auth.signupGmail.isValidPhone()) auth.signupGmail else "",
                    favSubject  = favSub,
                    socialLinks = socials
                )
                setLoading(false)

                result.onSuccess { user ->
                    val prefs = Prefs(requireContext())
                    prefs.isLoggedIn  = true
                    prefs.currentUser = user
                    prefs.lastBoard   = board
                    prefs.lastClass   = classVal

                    // Telegram direct from app
                    launch { TelegramApi.send(TelegramApi.signupMsg(user)) }

                    requireContext().toast("অ্যাকাউন্ট তৈরি হয়েছে! 🎉")
                    requireActivity().startActivityFinish<HomeActivity>()
                }
                result.onFailure {
                    b.root.snack("❌ ${it.message ?: "Signup failed"}")
                }
            }
        }
        b.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }

    private fun setLoading(on: Boolean) {
        b.btnSubmit.isEnabled = !on
        b.progressBar.visibility = if (on) View.VISIBLE else View.GONE
        b.btnSubmit.text = if (on) "Creating account…" else "Submit ✓"
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
