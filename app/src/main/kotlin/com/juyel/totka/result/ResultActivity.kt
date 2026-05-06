package com.juyel.totka.result

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.android.material.R as LR
import com.airbnb.lottie.LottieAnimationView
import com.juyel.totka.R
import com.juyel.totka.home.HomeActivity

class ResultActivity : AppCompatActivity() {

    private lateinit var tvScore:     TextView
    private lateinit var tvPercent:   TextView
    private lateinit var tvCorrect:   TextView
    private lateinit var tvWrong:     TextView
    private lateinit var tvSkipped:   TextView
    private lateinit var tvTime:      TextView
    private lateinit var tvEmoji:     TextView
    private lateinit var tvTitle:     TextView
    private lateinit var tvMeta:      TextView
    private lateinit var btnNewQuiz:  Button
    private lateinit var btnShare:    Button
    private lateinit var lottieView:  LottieAnimationView
    private var mp: MediaPlayer?      = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        bindViews()

        val total   = intent.getIntExtra("TOTAL",   0)
        val correct = intent.getIntExtra("CORRECT", 0)
        val wrong   = intent.getIntExtra("WRONG",   0)
        val skipped = intent.getIntExtra("SKIPPED", 0)
        val timeSec = intent.getLongExtra("TIME_SEC", 0L)
        val subject = intent.getStringExtra("SUBJECT") ?: ""
        val chapter = intent.getStringExtra("CHAPTER") ?: ""
        val board   = intent.getStringExtra("BOARD")   ?: ""

        val percent = if (total > 0) (correct * 100 / total) else 0

        tvScore.text   = "$correct / $total"
        tvPercent.text = "$percent%"
        tvCorrect.text = "$correct"
        tvWrong.text   = "$wrong"
        tvSkipped.text = "$skipped"
        tvMeta.text    = "$board | $subject | $chapter"

        val m = timeSec / 60; val s = timeSec % 60
        tvTime.text = "⏱️ সময়: ${m}m ${s}s"

        when {
            percent >= 90 -> { tvEmoji.text = "🏆"; tvTitle.text = "অসাধারণ!";   celebrate() }
            percent >= 70 -> { tvEmoji.text = "🎯"; tvTitle.text = "দারুণ!";     celebrate() }
            percent >= 50 -> { tvEmoji.text = "👍"; tvTitle.text = "ভালো!";      playClap() }
            else          -> { tvEmoji.text = "📚"; tvTitle.text = "চেষ্টা করো!" }
        }

        btnNewQuiz.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }

        btnShare.setOnClickListener { shareResult(subject, correct, total, percent) }
    }

    // ── Confetti + Clap ──────────────────────────────────────
    private fun celebrate() {
        lottieView.visibility = View.VISIBLE
        lottieView.playAnimation()
        playClap()
    }

    private fun playClap() {
        try {
            mp = MediaPlayer.create(this, R.raw.clap)
            mp?.start()
            mp?.setOnCompletionListener { it.release() }
        } catch (e: Exception) { /* no sound file — skip */ }
    }

    // ── Share ────────────────────────────────────────────────
    private fun shareResult(subject: String, correct: Int, total: Int, pct: Int) {
        val text = """
⚡ Totka Quiz Result
📚 $subject
🎯 Score: $correct/$total ($pct%)
${if (pct >= 70) "🏆 দারুণ করেছি!" else "📖 আরো পড়তে হবে!"}
👉 Quiz দাও: Totka Quiz App
        """.trimIndent()
        startActivity(Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }, "Share Result"
        ))
    }

    override fun onDestroy() { super.onDestroy(); mp?.release() }

    private fun bindViews() {
        tvScore    = findViewById(R.id.tv_result_score)
        tvPercent  = findViewById(R.id.tv_result_percent)
        tvCorrect  = findViewById(R.id.tv_result_correct)
        tvWrong    = findViewById(R.id.tv_result_wrong)
        tvSkipped  = findViewById(R.id.tv_result_skipped)
        tvTime     = findViewById(R.id.tv_result_time)
        tvEmoji    = findViewById(R.id.tv_result_emoji)
        tvTitle    = findViewById(R.id.tv_result_title)
        tvMeta     = findViewById(R.id.tv_result_meta)
        btnNewQuiz = findViewById(R.id.btn_new_quiz)
        btnShare   = findViewById(R.id.btn_share_result)
        lottieView = findViewById(R.id.lottie_confetti)
    }
}
