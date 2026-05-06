package com.juyel.totka.profile

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.juyel.totka.R
import com.juyel.totka.data.ApiService
import com.juyel.totka.data.TelegramService
import com.juyel.totka.utils.AppPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etUsername:   EditText
    private lateinit var etBio:        EditText
    private lateinit var etFavSubject: EditText
    private lateinit var etFacebook:   EditText
    private lateinit var etInstagram:  EditText
    private lateinit var etYoutube:    EditText
    private lateinit var etTelegram:   EditText
    private lateinit var btnSave:      Button
    private lateinit var progressBar:  ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        bindViews()
        prefillData()

        btnSave.setOnClickListener { saveChanges() }
        findViewById<ImageButton>(R.id.btn_edit_back).setOnClickListener { finish() }
    }

    private fun prefillData() {
        val user   = AppPrefs.getUser() ?: return
        val social = user.socialLinks
        etUsername.setText(user.username)
        etBio.setText(user.bio)
        etFavSubject.setText(user.favSubject)
        etFacebook.setText(social.facebook)
        etInstagram.setText(social.instagram)
        etYoutube.setText(social.youtube)
        etTelegram.setText(social.telegram)
    }

    private fun saveChanges() {
        val user = AppPrefs.getUser() ?: return
        setLoading(true)

        val newUsername   = etUsername.text.toString().trim()
        val newBio        = etBio.text.toString().trim()
        val newFav        = etFavSubject.text.toString().trim()
        val newFacebook   = etFacebook.text.toString().trim()
        val newInstagram  = etInstagram.text.toString().trim()
        val newYoutube    = etYoutube.text.toString().trim()
        val newTelegram   = etTelegram.text.toString().trim()

        // Build change log for Telegram
        val changeLog = mutableListOf<String>()
        if (newUsername  != user.username)           changeLog.add("*username*: `${user.username}` → `$newUsername`")
        if (newBio       != user.bio)                changeLog.add("*bio* updated")
        if (newFav       != user.favSubject)         changeLog.add("*favSubject*: `${user.favSubject}` → `$newFav`")
        if (newFacebook  != user.socialLinks.facebook)  changeLog.add("*facebook* updated")
        if (newInstagram != user.socialLinks.instagram) changeLog.add("*instagram* updated")
        if (newYoutube   != user.socialLinks.youtube)   changeLog.add("*youtube* updated")
        if (newTelegram  != user.socialLinks.telegram)  changeLog.add("*telegram* updated")

        val changes = mapOf(
            "username"    to newUsername,
            "bio"         to newBio,
            "favSubject"  to newFav,
            "socialLinks" to mapOf(
                "facebook" to newFacebook, "instagram" to newInstagram,
                "youtube"  to newYoutube,  "telegram"  to newTelegram,
            )
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val ok = suspendCancellableCoroutine<Boolean> { c ->
                ApiService.updateProfile(user.userId, changes) { c.resume(it) }
            }
            launch(Dispatchers.Main) {
                setLoading(false)
                if (ok) {
                    // Update local SharedPrefs
                    val updated = user.copy(
                        username   = newUsername,
                        bio        = newBio,
                        favSubject = newFav,
                        socialLinks = user.socialLinks.copy(
                            facebook  = newFacebook,
                            instagram = newInstagram,
                            youtube   = newYoutube,
                            telegram  = newTelegram,
                        )
                    )
                    AppPrefs.saveUser(updated)

                    // Telegram notification from App directly
                    if (changeLog.isNotEmpty()) {
                        TelegramService.notifyProfileEdit(newUsername, changeLog)
                    }

                    Toast.makeText(this@EditProfileActivity,
                        "✅ প্রোফাইল আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditProfileActivity,
                        "Update failed. Try again.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLoading(on: Boolean) {
        progressBar.visibility = if (on) View.VISIBLE else View.GONE
        btnSave.isEnabled      = !on
    }

    private fun bindViews() {
        etUsername   = findViewById(R.id.et_edit_username)
        etBio        = findViewById(R.id.et_edit_bio)
        etFavSubject = findViewById(R.id.et_edit_fav)
        etFacebook   = findViewById(R.id.et_edit_facebook)
        etInstagram  = findViewById(R.id.et_edit_instagram)
        etYoutube    = findViewById(R.id.et_edit_youtube)
        etTelegram   = findViewById(R.id.et_edit_telegram)
        btnSave      = findViewById(R.id.btn_save_profile)
        progressBar  = findViewById(R.id.edit_progress)
    }
}
