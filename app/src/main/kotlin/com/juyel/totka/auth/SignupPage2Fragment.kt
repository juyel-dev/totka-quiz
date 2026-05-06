package com.juyel.totka.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.juyel.totka.R
import com.juyel.totka.databinding.FragmentSignupP2Binding

class SignupPage2Fragment : Fragment() {

    private var _b: FragmentSignupP2Binding? = null
    private val b get() = _b!!
    private val vm: SignupViewModel by activityViewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            vm.profilePicUri = it
            Glide.with(this).load(it).circleCrop().into(b.imgAvatar)
        }
    }

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentSignupP2Binding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        b.etFullName.setText(vm.fullName)
        vm.profilePicUri?.let {
            Glide.with(this).load(it).circleCrop().into(b.imgAvatar)
        }

        b.btnPickPhoto.setOnClickListener { pickImage.launch("image/*") }

        b.btnNextP2.setOnClickListener {
            val name = b.etFullName.text.toString().trim()
            if (name.isBlank()) { b.tilFullName.error = "নাম দিন"; return@setOnClickListener }
            b.tilFullName.error = null
            vm.fullName = name
            (activity as SignupActivity).goNext()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
