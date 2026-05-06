package com.juyel.totka.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.juyel.totka.databinding.FragmentSignupP3Binding

class SignupPage3Fragment : Fragment() {

    private var _b: FragmentSignupP3Binding? = null
    private val b get() = _b!!
    private val vm: SignupViewModel by activityViewModels()

    private val boards  = listOf("NCTB / জাতীয়","SSC","HSC","JSC","Primary","CBSE","ICSE","অন্যান্য")
    private val classes = (1..12).map { "${it} শ্রেণি" } + listOf("HSC 1st Year","HSC 2nd Year","অন্যান্য")

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentSignupP3Binding.inflate(i, c, false).also { _b = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        val ctx = requireContext()

        b.spinnerBoard.adapter  = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, boards)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        b.spinnerClass.adapter  = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, classes)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Restore
        b.etUsername.setText(vm.username)
        b.etBio.setText(vm.bio)
        b.etPhone.setText(vm.phone)
        b.etFavSubject.setText(vm.favSubject)
        b.etFacebook.setText(vm.facebook)
        b.etInstagram.setText(vm.instagram)
        b.etYoutube.setText(vm.youtube)
        b.etTelegram.setText(vm.telegram)

        // No submit button here — Submit is in SignupActivity toolbar
        // But we still validate before submit via saveToVm()
    }

    fun saveToVm(): Boolean {
        vm.board      = b.spinnerBoard.selectedItem.toString()
        vm.classVal   = b.spinnerClass.selectedItem.toString()
        vm.username   = b.etUsername.text.toString().trim()
        vm.bio        = b.etBio.text.toString().trim()
        vm.phone      = b.etPhone.text.toString().trim()
        vm.favSubject = b.etFavSubject.text.toString().trim()
        vm.facebook   = b.etFacebook.text.toString().trim()
        vm.instagram  = b.etInstagram.text.toString().trim()
        vm.youtube    = b.etYoutube.text.toString().trim()
        vm.telegram   = b.etTelegram.text.toString().trim()
        return true
    }

    override fun onDestroyView() { super.onDestroyView(); _b = null }
}
