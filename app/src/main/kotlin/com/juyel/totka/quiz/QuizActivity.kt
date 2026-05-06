package com.juyel.totka.quiz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.juyel.totka.R
import com.juyel.totka.data.ApiService
import com.juyel.totka.data.model.Question
import com.juyel.totka.result.ResultActivity
import com.juyel.totka.utils.AppPrefs
import com.juyel.totka.utils.CsvParser
import com.juyel.totka.utils.StreakHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class QuizActivity : AppCompatActivity() {

    // ── Views ────────────────────────────────────────────────
    private lateinit var tvProgress:   TextView
    private lateinit var tvTimer:      TextView
    private lateinit var progressBar:  ProgressBar
    private lateinit var tvQuestion:   TextView
    private lateinit var btnA: Button; private lateinit var btnB: Button
    private lateinit var btnC: Button; private lateinit var btnD: Button
    private lateinit var btnPrev:      Button
    private lateinit var btnNext:      Button
    private lateinit var btnSubmit:    Button
    private lateinit var tvExplanation: TextView
    private lateinit var layoutExplain: LinearLayout
    private lateinit var btnBookmark:  ImageButton
    private lateinit var loadingView:  View

    // ── State ────────────────────────────────────────────────
    private var questions: MutableList<Question> = mutableListOf()
    private var currentIdx = 0
    private var timer: CountDownTimer? = null
    private var elapsedSec = 0L
    private lateinit var board: String
    private lateinit var subject: String
    private lateinit var chapter: String
    private lateinit var classVal: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        bindViews()

        board    = intent.getStringExtra("BOARD")   ?: ""
        subject  = intent.getStringExtra("SUBJECT") ?: ""
        chapter  = intent.getStringExtra("CHAPTER") ?: ""
        classVal = intent.getStringExtra("CLASS")   ?: ""
        val csvUrls = intent.getStringArrayListExtra("CSV_URLS") ?: return
        val diff    = intent.getStringExtra("DIFF") ?: "All"
        val qCount  = intent.getIntExtra("QCOUNT", 10)

        loadQuestions(csvUrls, diff, qCount)

        btnA.setOnClickListener { onAnswer("A") }
        btnB.setOnClickListener { onAnswer("B") }
        btnC.setOnClickListener { onAnswer("C") }
        btnD.setOnClickListener { onAnswer("D") }
        btnPrev.setOnClickListener { navigate(-1) }
        btnNext.setOnClickListener { navigate(1) }
        btnSubmit.setOnClickListener { confirmSubmit() }
        btnBookmark.setOnClickListener { toggleBookmark() }
    }

    // ── Load questions from multiple CSVs ────────────────────
    private fun loadQuestions(urls: List<String>, diff: String, qCount: Int) {
        loadingView.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val allQ = mutableListOf<Question>()
            for (url in urls) {
                val csv = suspendCancellableCoroutine<String?> { c ->
                    ApiService.fetchChapterCsv(url) { c.resume(it) }
                }
                csv?.let { allQ.addAll(CsvParser.parseChapter(it)) }
            }
            // Filter by difficulty
            val filtered = if (diff == "All") allQ
                           else allQ.filter { it.difficulty.equals(diff, true) }
            val final = filtered.shuffled().take(qCount)

            launch(Dispatchers.Main) {
                loadingView.visibility = View.GONE
                if (final.isEmpty()) {
                    Toast.makeText(this@QuizActivity, "প্রশ্ন লোড হয়নি", Toast.LENGTH_LONG).show()
                    finish(); return@launch
                }
                questions.addAll(final)
                startTimer()
                showQuestion()
            }
        }
    }

    // ── Timer ────────────────────────────────────────────────
    private fun startTimer() {
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(ms: Long) {
                elapsedSec++
                val m = elapsedSec / 60; val s = elapsedSec % 60
                tvTimer.text = "${m.toString().padStart(2,'0')}:${s.toString().padStart(2,'0')}"
            }
            override fun onFinish() {}
        }.start()
    }

    // ── Show current question ────────────────────────────────
    private fun showQuestion() {
        if (questions.isEmpty()) return
        val q = questions[currentIdx]

        tvProgress.text = "${currentIdx + 1} / ${questions.size}"
        progressBar.max      = questions.size
        progressBar.progress = currentIdx + 1

        tvQuestion.text = q.question
        btnA.text = "A) ${q.optA}"; btnB.text = "B) ${q.optB}"
        btnC.text = "C) ${q.optC}"; btnD.text = "D) ${q.optD}"

        layoutExplain.visibility = View.GONE
        resetOptionColors()

        // Restore answer if already answered
        if (q.userAnswer.isNotEmpty()) highlightAnswer(q)

        btnPrev.isEnabled    = currentIdx > 0
        btnNext.visibility   = if (currentIdx < questions.size - 1) View.VISIBLE else View.GONE
        btnSubmit.visibility = if (currentIdx == questions.size - 1) View.VISIBLE else View.GONE

        // Bookmark icon
        btnBookmark.setImageResource(
            if (q.bookmarked) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )
    }

    // ── Handle answer tap ────────────────────────────────────
    private fun onAnswer(choice: String) {
        val q = questions[currentIdx]
        if (q.userAnswer.isNotEmpty()) return   // already answered
        q.userAnswer = choice
        highlightAnswer(q)
        if (q.explanation.isNotEmpty()) {
            tvExplanation.text = q.explanation
            layoutExplain.visibility = View.VISIBLE
        }
    }

    private fun highlightAnswer(q: Question) {
        val buttons = mapOf("A" to btnA, "B" to btnB, "C" to btnC, "D" to btnD)
        buttons.forEach { (key, btn) ->
            btn.setBackgroundResource(
                when {
                    key == q.correct                       -> R.drawable.bg_correct
                    key == q.userAnswer && key != q.correct -> R.drawable.bg_wrong
                    else                                   -> R.drawable.bg_input
                }
            )
        }
    }

    private fun resetOptionColors() {
        listOf(btnA, btnB, btnC, btnD).forEach {
            it.setBackgroundResource(R.drawable.bg_input)
        }
    }

    // ── Navigate ─────────────────────────────────────────────
    private fun navigate(delta: Int) {
        val next = currentIdx + delta
        if (next in questions.indices) { currentIdx = next; showQuestion() }
    }

    // ── Bookmark ─────────────────────────────────────────────
    private fun toggleBookmark() {
        questions[currentIdx].bookmarked = !questions[currentIdx].bookmarked
        showQuestion()
    }

    // ── Submit ───────────────────────────────────────────────
    private fun confirmSubmit() {
        val unanswered = questions.count { it.userAnswer.isEmpty() }
        if (unanswered > 0) {
            AlertDialog.Builder(this)
                .setTitle("⚠️ এখনো $unanswered টি প্রশ্ন বাকি")
                .setMessage("জমা দেবে?")
                .setPositiveButton("হ্যাঁ, জমা দাও") { _, _ -> submitQuiz() }
                .setNegativeButton("না, আরো দেখি", null)
                .show()
        } else submitQuiz()
    }

    private fun submitQuiz() {
        timer?.cancel()
        StreakHelper.updateStreak()

        val correct = questions.count { it.userAnswer == it.correct }
        val wrong   = questions.count { it.userAnswer.isNotEmpty() && it.userAnswer != it.correct }
        val skipped = questions.count { it.userAnswer.isEmpty() }

        Intent(this, ResultActivity::class.java).apply {
            putExtra("TOTAL",    questions.size)
            putExtra("CORRECT",  correct)
            putExtra("WRONG",    wrong)
            putExtra("SKIPPED",  skipped)
            putExtra("TIME_SEC", elapsedSec)
            putExtra("SUBJECT",  subject)
            putExtra("CHAPTER",  chapter)
            putExtra("BOARD",    board)
            putExtra("CLASS",    classVal)
            startActivity(this)
        }
        finish()
    }

    override fun onDestroy() { super.onDestroy(); timer?.cancel() }

    private fun bindViews() {
        tvProgress    = findViewById(R.id.tv_progress)
        tvTimer       = findViewById(R.id.tv_timer)
        progressBar   = findViewById(R.id.quiz_progress_bar)
        tvQuestion    = findViewById(R.id.tv_question)
        btnA          = findViewById(R.id.btn_opt_a)
        btnB          = findViewById(R.id.btn_opt_b)
        btnC          = findViewById(R.id.btn_opt_c)
        btnD          = findViewById(R.id.btn_opt_d)
        btnPrev       = findViewById(R.id.btn_prev)
        btnNext       = findViewById(R.id.btn_next)
        btnSubmit     = findViewById(R.id.btn_submit_quiz)
        tvExplanation = findViewById(R.id.tv_explanation)
        layoutExplain = findViewById(R.id.layout_explanation)
        btnBookmark   = findViewById(R.id.btn_bookmark)
        loadingView   = findViewById(R.id.quiz_loading)
    }
}
