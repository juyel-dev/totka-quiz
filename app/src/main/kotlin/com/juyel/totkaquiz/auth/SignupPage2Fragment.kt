package com.juyel.totkaquiz.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.juyel.totkaquiz.data.Prefs
import com.juyel.totkaquiz.databinding.FragmentSignupP2Binding
import com.juyel.totkaquiz.utils.*

class SignupPage2Fragment : Fragment() {
    private var _b: FragmentSignupP2Binding? = null
    private val b get() = _b!!
    private var picUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            picUri = it
            Glide.with(this).load(it).circleCrop().into(b.imgProfile)
        }
    }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentSignupP2Binding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(view: View, s: Bundle?) {
        super.onViewCreated(view, s)
        val auth = activity as AuthActivity

        b.tvStep.text = "Step 2 / 3 — Profile Picture"
        b.imgProfile.setOnClickListener { pickImage.launch("image/*") }
        b.btnPickPhoto.setOnClickListener { pickImage.launch("image/*") }

        b.btnNext.setOnClickListener {
            val name = b.etFullName.text.toString().trim()
            if (name.isEmpty()) { b.root.snack("Enter your full name"); return@setOnClickListener }
            auth.signupName = name
            // Save profile picture URI locally
            picUri?.let { Prefs(requireContext()).profilePicUri = it.toString() }
            auth.goToSignupPage3()
        }
        b.btnBack.setOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }
    }
    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
