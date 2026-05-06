package com.juyel.totkaquiz.result

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.juyel.totkaquiz.data.*
import com.juyel.totkaquiz.databinding.ActivityResultBinding
import com.juyel.totkaquiz.home.HomeActivity
import com.juyel.totkaquiz.utils.*
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {

    private lateinit var b: ActivityResultBinding
    private lateinit var prefs: Prefs
    private lateinit var result: QuizResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityResultBinding.inflate(layoutInflater)
        setContentView(b.root)
        prefs  = Prefs(this)
        result = Gson().fromJson(intent.getStringExtra("result_json"), QuizResult::class.java)

        displayResult()
        setupButtons()

        // Send quiz result to Telegram
        prefs.currentUser?.let { user ->
            lifecycleScope.launch {
                TelegramApi.send(TelegramApi.quizResultMsg(user, result))
            }
        }
    }

    private fun displayResult() {
        val pct = result.percentage

        // Emoji & title
        val (emoji, title) = when {
            pct >= 90 -> "🏆" to "অসাধারণ!"
            pct >= 70 -> "🎯" to "দারুণ!"
            pct >= 50 -> "👍" to "ভালো!"
            else      -> "📚" to "আরো পড়ো!"
        }
        b.tvEmoji.text  = emoji
        b.tvTitle.text  = title
        b.tvMeta.text   = "${result.subject} • ${result.board} ${result.classVal}"
        b.tvTime.text   = "⏱️ সময়: ${result.timeSecs.toTimeString()}"

        // Score ring animation
        b.tvScore.text = "$pct%"
        b.scoreProgress.max      = 100
        b.scoreProgress.progress = pct

        // Stats
        b.tvCorrect.text = result.correct.toString()
        b.tvWrong.text   = result.wrong.toString()
        b.tvSkipped.text = result.skipped.toString()
        b.tvFinal.text   = "${result.correct} / ${result.totalQuestions}"

        // Streak badge
        b.tvStreak.text = "🔥 ${prefs.streakCount} day streak!"

        // Confetti + sound for good score
        if (pct >= 70) {
            ConfettiHelper.explode(b.konfettiView)
            ConfettiHelper.playClapSound(this)
        }
    }

    private fun setupButtons() {
        b.btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
        b.btnShare.setOnClickListener {
            val text = "🎯 Quiz Result\n" +
                "${result.subject} • ${result.classVal}\n" +
                "Score: ${result.correct}/${result.totalQuestions} (${result.percentage}%)\n" +
                "Time: ${result.timeSecs.toTimeString()}\n" +
                "\n#TotkaQuiz"
            startActivity(Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text)
                }, "Share Result"
            ))
        }
        b.btnNewQuiz.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }
}
