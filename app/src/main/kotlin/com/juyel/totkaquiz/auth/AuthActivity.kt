package com.juyel.totkaquiz.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.juyel.totkaquiz.R
import com.juyel.totkaquiz.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuthBinding

    // Shared signup data across pages
    var signupGmail    = ""
    var signupPassword = ""
    var signupName     = ""
    var signupUsername = ""
    var signupBio      = ""
    var signupBoard    = ""
    var signupClass    = ""
    var signupPhone    = ""
    var signupFavSub   = ""
    var signupSocials  = mutableMapOf<String, String>()
    var isSignupMode   = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            showFragment(LoginFragment())
        }
    }

    fun showFragment(fragment: Fragment, addToBack: Boolean = false) {
        val tx = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out
            )
            .replace(R.id.auth_container, fragment)
        if (addToBack) tx.addToBackStack(null)
        tx.commit()
    }

    fun goToSignupPage1() {
        isSignupMode = true
        showFragment(SignupPage1Fragment(), addToBack = true)
    }

    fun goToSignupPage2() = showFragment(SignupPage2Fragment(), addToBack = true)
    fun goToSignupPage3() = showFragment(SignupPage3Fragment(), addToBack = true)
    fun backToLogin()     = supportFragmentManager.popBackStack()
}
