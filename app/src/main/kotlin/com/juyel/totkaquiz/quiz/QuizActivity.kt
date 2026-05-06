package com.juyel.totkaquiz.quiz

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.juyel.totkaquiz.data.*
import com.juyel.totkaquiz.databinding.ActivityQuizBinding
import com.juyel.totkaquiz.result.ResultActivity
import com.juyel.totkaquiz.utils.*
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {

    private lateinit var b: ActivityQuizBinding
    private lateinit var prefs: Prefs

    private var questions    = mutableListOf<Question>()
    private var currentIndex = 0
    private var startTime    = 0L

    // Intent extras
    private lateinit var csvLinks:  List<String>
    private var subject     = ""
    private var chapter     = ""
    private var board       = ""
    private var classVal    = ""
    private var difficulty  = "all"
    private var maxCount    = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(b.root)
        prefs = Prefs(this)

        csvLinks  = intent.getStringArrayListExtra("csvLinks") ?: arrayListOf()
        subject   = intent.getStringExtra("subject")  ?: ""
        chapter   = intent.getStringExtra("chapter")  ?: ""
        board     = intent.getStringExtra("board")    ?: ""
        classVal  = intent.getStringExtra("classVal") ?: ""
        difficulty = intent.getStringExtra("difficulty") ?: "all"
        maxCount  = intent.getIntExtra("questionCount", 10)

        b.tvSubject.text = "$subject${if (chapter.isNotEmpty()) " • $chapter" else ""}"
        b.btnExit.setOnClickListener { confirmExit() }

        loadQuestions()
    }

    private fun loadQuestions() {
        b.progressLoad.show()
        b.quizContent.hide()

        lifecycleScope.launch {
            val allQ = mutableListOf<Question>()
            for (link in csvLinks) {
                CsvParser.fetchCsv(link).onSuccess { csv ->
                    allQ.addAll(CsvParser.parseQuestions(csv))
                }
            }

            // Filter difficulty
            val filtered = if (difficulty == "all") allQ
                           else allQ.filter { it.difficulty.equals(difficulty, true) }

            // Shuffle & take
            questions = filtered.shuffled().take(maxCount).toMutableList()

            if (questions.isEmpty()) {
                toast("⚠️ No questions found!")
                finish(); return@launch
            }

            b.progressLoad.hide()
            b.quizContent.show()
            startTime = SystemClock.elapsedRealtime()
            showQuestion(0)
        }
    }

    private fun showQuestion(index: Int) {
        currentIndex = index
        val q = questions[index]
        val total = questions.size

        b.tvProgress.text = "${index + 1} / $total"
        b.progressBar.max      = total
        b.progressBar.progress = index + 1
        b.tvDifficulty.text    = q.difficulty.capitalizeFirst()
        b.tvQuestion.text      = q.question
        b.tvChapter.text       = q.chapter

        // Options
        val opts = listOf(b.opt0, b.opt1, b.opt2, b.opt3)
        val labels = listOf("A", "B", "C", "D")
        opts.forEachIndexed { i, chip ->
            chip.text = "${labels[i]}. ${q.options.getOrElse(i) { "" }}"
            chip.isChecked  = false
            chip.isEnabled  = !q.isAnswered
            chip.alpha      = 1f
            chip.setChipBackgroundColorResource(com.google.android.material.R.color.design_default_color_background)
        }

        // Restore previous answer if answered
        if (q.isAnswered) {
            highlightAnswers(q)
            b.btnNext.show()
            b.btnSubmit.visibility = if (index == total - 1) View.VISIBLE else View.GONE
            b.layoutExplanation.show()
            b.tvExplanation.text = q.explanation.ifEmpty { "No explanation available." }
        } else {
            b.btnNext.hide()
            b.btnSubmit.hide()
            b.layoutExplanation.hide()
        }

        opts.forEachIndexed { i, chip ->
            chip.setOnClickListener {
                if (!q.isAnswered) {
                    q.userAnswer = labels[i]
                    highlightAnswers(q)
                    chip.isChecked = true
                    b.layoutExplanation.show()
                    b.tvExplanation.text = q.explanation.ifEmpty { "No explanation available." }
                    if (index == total - 1) b.btnSubmit.show() else b.btnNext.show()
                }
            }
        }

        b.btnNext.setOnClickListener {
            if (index < total - 1) showQuestion(index + 1)
        }
        b.btnSubmit.setOnClickListener { submitQuiz() }

        // Prev/Next navigation
        b.btnPrev.isEnabled = index > 0
        b.btnPrev.setOnClickListener { if (index > 0) showQuestion(index - 1) }

        // Bookmark
        val bookmarks = prefs.bookmarks
        b.btnBookmark.isChecked = bookmarks.contains(q.id)
        b.btnBookmark.setOnClickListener {
            val bk = prefs.bookmarks.toMutableList()
            if (bk.contains(q.id)) { bk.remove(q.id); toast("Bookmark removed") }
            else { bk.add(q.id); toast("Bookmarked! 🔖") }
            prefs.bookmarks = bk
        }
    }

    private fun highlightAnswers(q: Question) {
        val opts   = listOf(b.opt0, b.opt1, b.opt2, b.opt3)
        val labels = listOf("A", "B", "C", "D")
        opts.forEachIndexed { i, chip ->
            chip.isEnabled = false
            when (labels[i]) {
                q.correct    -> chip.setChipBackgroundColorResource(android.R.color.holo_green_dark)
                q.userAnswer -> if (!q.isCorrect) chip.setChipBackgroundColorResource(android.R.color.holo_red_dark)
            }
        }
    }

    private fun submitQuiz() {
        val elapsedSecs = (SystemClock.elapsedRealtime() - startTime) / 1000

        val correct = questions.count { it.isCorrect }
        val answered= questions.count { it.isAnswered }
        val wrong   = answered - correct
        val skipped = questions.size - answered

        val result = QuizResult(
            totalQuestions = questions.size,
            correct        = correct,
            wrong          = wrong,
            skipped        = skipped,
            timeSecs       = elapsedSecs,
            subject        = subject,
            chapter        = chapter,
            board          = board,
            classVal       = classVal
        )

        // Save history & update streak
        prefs.saveQuizResult(result)
        updateStreak()

        startActivity<ResultActivity> {
            putExtra("result_json", com.google.gson.Gson().toJson(result))
        }
        finish()
    }

    private fun updateStreak() {
        val today = todayString()
        if (prefs.lastQuizDate != today) {
            prefs.streakCount  = prefs.streakCount + 1
            prefs.lastQuizDate = today
        }
    }

    private fun confirmExit() {
        AlertDialog.Builder(this)
            .setTitle("Exit Quiz?")
            .setMessage("তোমার progress হারিয়ে যাবে।")
            .setPositiveButton("Exit") { _, _ -> finish() }
            .setNegativeButton("Stay", null)
            .show()
    }

    override fun onBackPressed() = confirmExit()
}
