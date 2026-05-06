package com.juyel.totka.profile

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.juyel.totka.R
import com.juyel.totka.auth.AuthActivity
import com.juyel.totka.utils.AppPrefs

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val user = AppPrefs.getUser()

        // Avatar
        val imgAvatar = findViewById<ImageView>(R.id.img_profile_avatar)
        AppPrefs.getAvatarUri()?.let {
            Glide.with(this).load(it).circleCrop().into(imgAvatar)
        }

        // Basic info
        findViewById<TextView>(R.id.tv_profile_name).text     = user?.fullName   ?: "—"
        findViewById<TextView>(R.id.tv_profile_username).text = "@${user?.username ?: "username"}"
        findViewById<TextView>(R.id.tv_profile_bio).text      = user?.bio.takeIf { !it.isNullOrBlank() } ?: "কোনো Bio নেই"
        findViewById<TextView>(R.id.tv_profile_board).text    = "🏫 Board: ${user?.board ?: "—"}"
        findViewById<TextView>(R.id.tv_profile_class).text    = "📚 Class: ${user?.classVal ?: "—"}"
        findViewById<TextView>(R.id.tv_profile_fav).text      = "⭐ ${user?.favSubject ?: "—"}"
        findViewById<TextView>(R.id.tv_profile_streak).text   = "🔥 Streak: ${AppPrefs.streak} days"
        findViewById<TextView>(R.id.tv_profile_joined).text   = "📅 Joined: ${user?.joinDate?.take(10) ?: "—"}"

        // Social links
        val social = user?.socialLinks
        showLink(R.id.tv_social_fb,  "Facebook",  social?.facebook)
        showLink(R.id.tv_social_ig,  "Instagram", social?.instagram)
        showLink(R.id.tv_social_yt,  "YouTube",   social?.youtube)
        showLink(R.id.tv_social_tg,  "Telegram",  social?.telegram)

        // Edit button
        findViewById<Button>(R.id.btn_edit_profile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // Logout
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            AppPrefs.logout()
            startActivity(Intent(this, AuthActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
        }

        // Back
        findViewById<ImageButton>(R.id.btn_profile_back).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        // Refresh after edit
        val user = AppPrefs.getUser()
        findViewById<TextView>(R.id.tv_profile_name).text     = user?.fullName ?: "—"
        findViewById<TextView>(R.id.tv_profile_username).text = "@${user?.username}"
        findViewById<TextView>(R.id.tv_profile_bio).text      = user?.bio ?: ""
    }

    private fun showLink(id: Int, label: String, url: String?) {
        val tv = findViewById<TextView>(id)
        if (url.isNullOrBlank()) { tv.visibility = android.view.View.GONE }
        else {
            tv.text = "🔗 $label"
            tv.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, android.net.Uri.parse(url)))
            }
        }
    }
}
