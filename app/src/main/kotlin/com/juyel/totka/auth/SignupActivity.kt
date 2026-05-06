package com.juyel.totka.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.juyel.totka.R
import com.juyel.totka.data.ApiService
import com.juyel.totka.data.TelegramService
import com.juyel.totka.data.model.SocialLinks
import com.juyel.totka.home.HomeActivity
import com.juyel.totka.utils.AppPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SignupActivity : AppCompatActivity() {

    // ── Pages ────────────────────────────────────────────────
    private lateinit var page1: View
    private lateinit var page2: View
    private lateinit var page3: View
    private var currentPage = 1

    // ── Page 1 ───────────────────────────────────────────────
    private lateinit var etGmail:    EditText
    private lateinit var etPassword: EditText

    // ── Page 2 ───────────────────────────────────────────────
    private lateinit var imgAvatar:  ImageView
    private lateinit var etFullName: EditText
    private var avatarUri: Uri?      = null

    // ── Page 3 ───────────────────────────────────────────────
    private lateinit var spinBoard:   Spinner
    private lateinit var spinClass:   Spinner
    private lateinit var etUsername:  EditText
    private lateinit var etBio:       EditText
    private lateinit var etFavSubject: EditText
    private lateinit var etFacebook:  EditText
    private lateinit var etInstagram: EditText
    private lateinit var etYoutube:   EditText
    private lateinit var etTelegram:  EditText

    // ── Nav buttons ──────────────────────────────────────────
    private lateinit var btnBack:   Button
    private lateinit var btnNext:   Button
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvPageIndicator: TextView

    // ── Image picker ─────────────────────────────────────────
    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            avatarUri = it
            Glide.with(this).load(it).circleCrop().into(imgAvatar)
        }
    }

    // ── Board / Class data ───────────────────────────────────
    private val boards  = listOf("NCTB","SSC","HSC","JSC","CBSE","ICSE","Cambridge","Others")
    private val classes = listOf("Class 1","Class 2","Class 3","Class 4","Class 5",
        "Class 6","Class 7","Class 8","Class 9","Class 10","Class 11","Class 12")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        bindViews()
        setupSpinners()
        showPage(1)

        imgAvatar.setOnClickListener { pickImage.launch("image/*") }

        btnBack.setOnClickListener {
            if (currentPage > 1) showPage(currentPage - 1)
            else finish()
        }
        btnNext.setOnClickListener {
            if (validateCurrentPage()) showPage(currentPage + 1)
        }
        btnSubmit.setOnClickListener { doSignup() }
    }

    private fun bindViews() {
        page1 = findViewById(R.id.page_1)
        page2 = findViewById(R.id.page_2)
        page3 = findViewById(R.id.page_3)

        etGmail    = findViewById(R.id.et_signup_gmail)
        etPassword = findViewById(R.id.et_signup_password)
        imgAvatar  = findViewById(R.id.img_avatar)
        etFullName = findViewById(R.id.et_full_name)
        spinBoard  = findViewById(R.id.spin_board)
        spinClass  = findViewById(R.id.spin_class)
        etUsername  = findViewById(R.id.et_username)
        etBio       = findViewById(R.id.et_bio)
        etFavSubject= findViewById(R.id.et_fav_subject)
        etFacebook  = findViewById(R.id.et_facebook)
        etInstagram = findViewById(R.id.et_instagram)
        etYoutube   = findViewById(R.id.et_youtube)
        etTelegram  = findViewById(R.id.et_telegram)

        btnBack      = findViewById(R.id.btn_back_page)
        btnNext      = findViewById(R.id.btn_next_page)
        btnSubmit    = findViewById(R.id.btn_submit)
        progressBar  = findViewById(R.id.signup_progress)
        tvPageIndicator = findViewById(R.id.tv_page_indicator)
    }

    private fun setupSpinners() {
        spinBoard.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, boards)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinClass.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classes)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun showPage(page: Int) {
        currentPage = page
        page1.visibility = if (page == 1) View.VISIBLE else View.GONE
        page2.visibility = if (page == 2) View.VISIBLE else View.GONE
        page3.visibility = if (page == 3) View.VISIBLE else View.GONE

        tvPageIndicator.text = "Page $page / 3"
        btnBack.visibility   = View.VISIBLE
        btnNext.visibility   = if (page < 3) View.VISIBLE else View.GONE
        btnSubmit.visibility = if (page == 3) View.VISIBLE else View.GONE
    }

    private fun validateCurrentPage(): Boolean {
        return when (currentPage) {
            1 -> {
                if (etGmail.text.isBlank() || etPassword.text.isBlank()) {
                    Toast.makeText(this, getString(R.string.err_fill_all), Toast.LENGTH_SHORT).show()
                    false
                } else true
            }
            2 -> {
                if (etFullName.text.isBlank()) {
                    Toast.makeText(this, "নাম দাও", Toast.LENGTH_SHORT).show()
                    false
                } else true
            }
            else -> true
        }
    }

    private fun doSignup() {
        setLoading(true)

        // Save avatar locally if picked
        avatarUri?.let { AppPrefs.prefs.edit().putString("avatar_uri", it.toString()).apply() }

        val social = SocialLinks(
            facebook  = etFacebook.text.toString().trim(),
            instagram = etInstagram.text.toString().trim(),
            youtube   = etYoutube.text.toString().trim(),
            telegram  = etTelegram.text.toString().trim(),
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val result = suspendCancellableCoroutine { cont ->
                ApiService.signup(
                    gmail       = etGmail.text.toString().trim(),
                    password    = etPassword.text.toString().trim(),
                    fullName    = etFullName.text.toString().trim(),
                    username    = etUsername.text.toString().trim(),
                    bio         = etBio.text.toString().trim(),
                    board       = spinBoard.selectedItem.toString(),
                    classVal    = spinClass.selectedItem.toString(),
                    phone       = "",
                    favSubject  = etFavSubject.text.toString().trim(),
                    socialLinks = social,
                ) { user -> cont.resume(user) }
            }
            launch(Dispatchers.Main) {
                setLoading(false)
                if (result != null) {
                    AppPrefs.saveUser(result)
                    TelegramService.notifySignup(
                        result.fullName, result.gmail, result.board, result.classVal
                    )
                    startActivity(Intent(this@SignupActivity, HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    finish()
                } else {
                    Toast.makeText(this@SignupActivity, "Signup failed. Try again.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        btnSubmit.isEnabled    = !loading
    }
}
