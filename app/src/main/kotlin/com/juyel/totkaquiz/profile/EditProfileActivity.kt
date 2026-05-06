package com.juyel.totkaquiz.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.juyel.totkaquiz.data.*
import com.juyel.totkaquiz.databinding.ActivityEditProfileBinding
import com.juyel.totkaquiz.utils.*
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var b: ActivityEditProfileBinding
    private lateinit var prefs: Prefs
    private val gasApi = GasApi()
    private var newPicUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            newPicUri = it
            Glide.with(this).load(it).circleCrop().into(b.imgProfile)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(b.root)
        prefs = Prefs(this)

        b.btnBack.setOnClickListener { finish() }
        b.imgProfile.setOnClickListener { pickImage.launch("image/*") }
        b.btnChangePic.setOnClickListener { pickImage.launch("image/*") }
        b.btnSave.setOnClickListener { saveProfile() }

        prefillFields()
    }

    private fun prefillFields() {
        val user = prefs.currentUser ?: return
        b.etFullName.setText(user.fullName)
        b.etUsername.setText(user.username)
        b.etBio.setText(user.bio)
        b.etFavSubject.setText(user.favSubject)
        b.etFacebook.setText(user.socialLinks["facebook"] ?: "")
        b.etInstagram.setText(user.socialLinks["instagram"] ?: "")
        b.etYoutube.setText(user.socialLinks["youtube"] ?: "")
        b.etTelegram.setText(user.socialLinks["telegram"] ?: "")

        prefs.profilePicUri?.let {
            Glide.with(this).load(Uri.parse(it)).circleCrop().into(b.imgProfile)
        }
    }

    private fun saveProfile() {
        val user = prefs.currentUser ?: return
        val changes = mutableMapOf<String, Any?>()

        val newName    = b.etFullName.text.toString().trim()
        val newUser    = b.etUsername.text.toString().trim()
        val newBio     = b.etBio.text.toString().trim()
        val newFavSub  = b.etFavSubject.text.toString().trim()
        val newSocials = mapOf(
            "facebook"  to b.etFacebook.text.toString().trim(),
            "instagram" to b.etInstagram.text.toString().trim(),
            "youtube"   to b.etYoutube.text.toString().trim(),
            "telegram"  to b.etTelegram.text.toString().trim()
        ).filter { it.value.isNotEmpty() }

        if (newName.isEmpty())  { toast("Name cannot be empty"); return }
        if (newUser.isEmpty())  { toast("Username cannot be empty"); return }

        if (newName   != user.fullName)    changes["fullName"]    = newName
        if (newUser   != user.username)    changes["username"]    = newUser
        if (newBio    != user.bio)         changes["bio"]         = newBio
        if (newFavSub != user.favSubject)  changes["favSubject"]  = newFavSub
        if (newSocials != user.socialLinks) changes["socialLinks"] = newSocials

        // Profile pic — local only
        newPicUri?.let { prefs.profilePicUri = it.toString() }

        if (changes.isEmpty() && newPicUri == null) {
            toast("কোনো পরিবর্তন নেই"); return
        }

        if (changes.isEmpty()) { toast("Profile picture updated! ✅"); finish(); return }

        setLoading(true)

        lifecycleScope.launch {
            val result = gasApi.updateProfile(user.userId, changes)
            setLoading(false)

            result.onSuccess {
                // Update local user object
                val updatedUser = user.copy(
                    fullName    = newName,
                    username    = newUser,
                    bio         = newBio,
                    favSubject  = newFavSub,
                    socialLinks = user.socialLinks + newSocials
                )
                prefs.currentUser = updatedUser

                // Send Telegram message directly from app
                val log = changes.map { "• *${it.key}* → `${it.value}`" }
                launch { TelegramApi.send(TelegramApi.profileEditMsg(updatedUser, log)) }

                toast("Profile updated! ✅")
                finish()
            }
            result.onFailure {
                toast("❌ ${it.message}")
            }
        }
    }

    private fun setLoading(on: Boolean) {
        b.btnSave.isEnabled = !on
        b.progressBar.visibility = if (on) View.VISIBLE else View.GONE
        b.btnSave.text = if (on) "Saving…" else "Save Changes"
    }
}
