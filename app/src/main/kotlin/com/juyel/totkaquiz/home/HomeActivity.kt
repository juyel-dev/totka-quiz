package com.juyel.totkaquiz.home

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.juyel.totkaquiz.data.*
import com.juyel.totkaquiz.databinding.ActivityHomeBinding
import com.juyel.totkaquiz.profile.ProfileActivity
import com.juyel.totkaquiz.quiz.QuizActivity
import com.juyel.totkaquiz.utils.*
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var b: ActivityHomeBinding
    private lateinit var prefs: Prefs
    private val gasApi = GasApi()

    private var masterRows   = listOf<MasterRow>()
    private var selectedBoard   = ""
    private var selectedClass   = ""
    private var selectedSubject = ""
    private var selectedChapter = ""
    private var selectedDiff    = "all"
    private var questionCount   = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(b.root)
        prefs = Prefs(this)

        setupProfile()
        setupSlider()
        setupDifficulty()
        loadMaster()

        b.btnStartQuiz.setOnClickListener { startQuiz() }
        b.btnProfile.setOnClickListener { startActivity<ProfileActivity>() }
        b.btnHistory.setOnClickListener { toast("Quiz History — coming soon!") }
    }

    private fun setupProfile() {
        val user = prefs.currentUser ?: return
        b.tvWelcome.text = "হ্যালো, ${user.fullName.split(" ").first()} 👋"
        b.tvBoard.text   = "${user.board} • ${user.classVal}"
        prefs.profilePicUri?.let { uri ->
            Glide.with(this).load(uri).circleCrop().into(b.imgAvatar)
        }
        // Streak
        val today = todayString()
        if (prefs.lastQuizDate != today) b.tvStreak.text = "🔥 ${prefs.streakCount}"
        else b.tvStreak.text = "🔥 ${prefs.streakCount}"
    }

    private fun loadMaster() {
        b.progressMaster.show()
        b.cardFilters.hide()
        b.btnStartQuiz.disable()

        lifecycleScope.launch {
            // Try cache first
            val cached = prefs.masterCache
            if (cached != null && System.currentTimeMillis() - prefs.masterCacheTs < 5 * 60 * 1000) {
                masterRows = CsvParser.parseMaster(cached)
                setupBoardSpinner()
                b.progressMaster.hide()
                b.cardFilters.show()
                return@launch
            }

            // Fetch fresh CSV
            val result = CsvParser.fetchCsv(AppConfig.MASTER_CSV)
            result.onSuccess { csv ->
                prefs.masterCache   = csv
                prefs.masterCacheTs = System.currentTimeMillis()
                masterRows = CsvParser.parseMaster(csv)
                setupBoardSpinner()
                b.progressMaster.hide()
                b.cardFilters.show()
            }
            result.onFailure {
                b.progressMaster.hide()
                toast("⚠️ Could not load data. Check internet.")
            }
        }
    }

    private fun setupBoardSpinner() {
        val boards = masterRows.map { it.board }.distinct().filter { it.isNotEmpty() }
        val allBoards = listOf("— Select Board —") + boards
        b.spinnerBoard.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, allBoards)

        // Auto-select user's board
        val userBoard = prefs.lastBoard
        val idx = allBoards.indexOf(userBoard)
        if (idx > 0) b.spinnerBoard.setSelection(idx)

        b.spinnerBoard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedBoard = if (pos == 0) "" else allBoards[pos]
                setupClassSpinner()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun setupClassSpinner() {
        val classes = masterRows.filter { it.board == selectedBoard }
            .map { it.classVal }.distinct().filter { it.isNotEmpty() }
        val all = listOf("— Select Class —") + classes
        b.spinnerClass.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, all)

        val userClass = prefs.lastClass
        val idx = all.indexOf(userClass)
        if (idx > 0) b.spinnerClass.setSelection(idx)

        b.spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedClass = if (pos == 0) "" else all[pos]
                setupSubjectSpinner()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun setupSubjectSpinner() {
        val subjects = masterRows.filter { it.board == selectedBoard && it.classVal == selectedClass }
            .map { it.subject }.distinct().filter { it.isNotEmpty() }
        val all = listOf("— Select Subject —") + subjects
        b.spinnerSubject.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, all)
        b.spinnerSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedSubject = if (pos == 0) "" else all[pos]
                setupChapterSpinner()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun setupChapterSpinner() {
        val chapters = masterRows.filter {
            it.board == selectedBoard && it.classVal == selectedClass && it.subject == selectedSubject
        }.map { it.chapter }.filter { it.isNotEmpty() }
        val all = listOf("All Chapters") + chapters
        b.spinnerChapter.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, all)
        b.spinnerChapter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                selectedChapter = if (pos == 0) "" else all[pos]
                updateStartBtn()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun setupSlider() {
        b.sliderCount.addOnChangeListener { _, value, _ ->
            questionCount = value.toInt()
            b.tvCountVal.text = value.toInt().toString()
        }
    }

    private fun setupDifficulty() {
        b.chipAll.isChecked    = true
        selectedDiff            = "all"
        val chips = mapOf(b.chipAll to "all", b.chipEasy to "easy",
            b.chipMedium to "medium", b.chipHard to "hard")
        chips.forEach { (chip, diff) ->
            chip.setOnClickListener { selectedDiff = diff; chips.keys.forEach { it.isChecked = false }; chip.isChecked = true }
        }
    }

    private fun updateStartBtn() {
        val ready = selectedBoard.isNotEmpty() && selectedClass.isNotEmpty() && selectedSubject.isNotEmpty()
        if (ready) b.btnStartQuiz.enable() else b.btnStartQuiz.disable()
    }

    private fun startQuiz() {
        val rows = masterRows.filter {
            it.board == selectedBoard &&
            it.classVal == selectedClass &&
            it.subject == selectedSubject &&
            (selectedChapter.isEmpty() || it.chapter == selectedChapter)
        }
        if (rows.isEmpty()) { toast("No questions found for this selection"); return }

        val csvLinks = rows.map { it.csvLink }.filter { it.isNotEmpty() }

        startActivity<QuizActivity> {
            putStringArrayListExtra("csvLinks",    ArrayList(csvLinks))
            putExtra("subject",      selectedSubject)
            putExtra("chapter",      selectedChapter)
            putExtra("board",        selectedBoard)
            putExtra("classVal",     selectedClass)
            putExtra("difficulty",   selectedDiff)
            putExtra("questionCount", questionCount)
        }
    }

    override fun onResume() {
        super.onResume()
        setupProfile()
    }
}
