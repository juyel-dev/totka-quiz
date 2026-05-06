package com.juyel.totkaquiz.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.juyel.totkaquiz.auth.AuthActivity
import com.juyel.totkaquiz.data.Prefs
import com.juyel.totkaquiz.databinding.ActivityProfileBinding
import com.juyel.totkaquiz.utils.startActivity
import com.juyel.totkaquiz.utils.startActivityFinish
import com.juyel.totkaquiz.utils.toast

class ProfileActivity : AppCompatActivity() {

    private lateinit var b: ActivityProfileBinding
    private lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(b.root)
        prefs = Prefs(this)

        b.btnBack.setOnClickListener { finish() }
        b.btnEdit.setOnClickListener { startActivity<EditProfileActivity>() }
        b.btnLogout.setOnClickListener { logout() }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        val user = prefs.currentUser ?: return

        b.tvName.text     = user.fullName
        b.tvUsername.text = "@${user.username.ifEmpty{"—"}}"
        b.tvBio.text      = user.bio.ifEmpty{"No bio yet."}
        b.tvBoard.text    = "${user.board} • ${user.classVal}"
        b.tvFavSub.text   = "⭐ Fav: ${user.favSubject.ifEmpty{"—"}}"
        b.tvJoined.text   = "📅 Joined: ${user.joinDate.take(10)}"
        b.tvEmail.text    = user.gmail

        // Profile picture
        prefs.profilePicUri?.let {
            Glide.with(this).load(Uri.parse(it)).circleCrop()
                .placeholder(com.juyel.totkaquiz.R.drawable.ic_avatar)
                .into(b.imgProfile)
        }

        // Stats
        val history = prefs.getQuizHistory()
        b.tvTotalQuiz.text = "${history.size}"
        b.tvAvgScore.text  = if (history.isEmpty()) "—"
            else "${history.map { it.percentage }.average().toInt()}%"
        b.tvStreak.text    = "🔥 ${prefs.streakCount}"

        // Social links
        val social = user.socialLinks
        setupSocialBtn(b.btnFacebook, social["facebook"])
        setupSocialBtn(b.btnInstagram, social["instagram"])
        setupSocialBtn(b.btnYoutube, social["youtube"])
        setupSocialBtn(b.btnTelegram, social["telegram"])
    }

    private fun setupSocialBtn(btn: com.google.android.material.button.MaterialButton, url: String?) {
        if (url.isNullOrEmpty()) { btn.alpha = 0.3f; btn.isEnabled = false }
        else {
            btn.alpha   = 1f
            btn.isEnabled = true
            btn.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } catch(e: Exception) { toast("Cannot open link") }
            }
        }
    }

    private fun logout() {
        prefs.isLoggedIn  = false
        prefs.currentUser = null
        startActivityFinish<AuthActivity>()
    }
}
