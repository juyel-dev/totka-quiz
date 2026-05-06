package com.juyel.totkaquiz.auth

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.juyel.totkaquiz.databinding.FragmentSignupP1Binding
import com.juyel.totkaquiz.utils.*

class SignupPage1Fragment : Fragment() {
    private var _b: FragmentSignupP1Binding? = null
    private val b get() = _b!!

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentSignupP1Binding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        val auth = activity as AuthActivity

        b.tvStep.text = "Step 1 / 3 — Account Info"
        b.btnNext.setOnClickListener {
            val gmail = b.etGmail.text.toString().trim()
            val pass  = b.etPassword.text.toString().trim()
            val conf  = b.etConfirmPassword.text.toString().trim()

            when {
                gmail.isEmpty() -> b.root.snack("Enter Gmail or Phone")
                !gmail.isValidEmail() && !gmail.isValidPhone() ->
                    b.root.snack("Enter valid Gmail or phone")
                pass.length < 6 -> b.root.snack("Password must be 6+ characters")
                pass != conf    -> b.root.snack("Passwords don't match")
                else -> {
                    auth.signupGmail    = gmail
                    auth.signupPassword = pass
                    auth.goToSignupPage2()
                }
            }
        }
        b.btnBack.setOnClickListener { auth.backToLogin() }
    }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
