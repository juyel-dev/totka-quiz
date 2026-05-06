package com.juyel.totka.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.juyel.totka.databinding.FragmentSignupP1Binding

class SignupPage1Fragment : Fragment() {

    private var _b: FragmentSignupP1Binding? = null
    private val b get() = _b!!
    private val vm: SignupViewModel by activityViewModels()

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentSignupP1Binding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        // Restore values if user came back
        b.etGmail.setText(vm.gmail)
        b.etPassword.setText(vm.password)

        b.btnNextP1.setOnClickListener {
            val gmail = b.etGmail.text.toString().trim()
            val pass  = b.etPassword.text.toString()
            val conf  = b.etConfirmPassword.text.toString()

            when {
                gmail.isBlank()           -> b.tilGmail.error = "Gmail বা Phone নম্বর দিন"
                pass.length < 6           -> b.tilPassword.error = "কমপক্ষে ৬ অক্ষর"
                pass != conf              -> b.tilConfirmPassword.error = "Password মিলছে না"
                else -> {
                    b.tilGmail.error = null
                    b.tilPassword.error = null
                    b.tilConfirmPassword.error = null
                    vm.gmail    = gmail
                    vm.password = pass
                    (activity as SignupActivity).goNext()
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
