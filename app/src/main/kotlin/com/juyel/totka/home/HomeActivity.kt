package com.juyel.totka.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.juyel.totka.R
import com.juyel.totka.data.ApiService
import com.juyel.totka.data.model.MasterRow
import com.juyel.totka.profile.ProfileActivity
import com.juyel.totka.quiz.QuizActivity
import com.juyel.totka.utils.AppPrefs
import com.juyel.totka.utils.CsvParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class HomeActivity : AppCompatActivity() {

    private lateinit var spinBoard:   Spinner
    private lateinit var spinClass:   Spinner
    private lateinit var spinSubject: Spinner
    private lateinit var spinChapter: Spinner
    private lateinit var spinDiff:    Spinner
    private lateinit var seekQCount:  SeekBar
    private lateinit var tvQCount:    TextView
    private lateinit var btnStart:    Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStreak:    TextView
    private lateinit var imgAvatar:   ImageView
    private lateinit var tvName:      TextView

    private var masterRows: List<MasterRow> = emptyList()
    private var qCount = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bindViews()
        loadUserHeader()
        loadMaster()

        seekQCount.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, p: Int, u: Boolean) {
                qCount = maxOf(5, (p / 5) * 5)
                tvQCount.text = "$qCount প্রশ্ন"
            }
            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })
        btnStart.setOnClickListener { startQuiz() }
        imgAvatar.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun loadUserHeader() {
        val user = AppPrefs.getUser() ?: return
        tvName.text   = "হ্যালো, ${user.fullName.split(" ").first()}! 👋"
        tvStreak.text = "🔥 Streak: ${AppPrefs.streak}"
        val uri = AppPrefs.getAvatarUri()
        if (uri != null) Glide.with(this).load(uri).circleCrop().into(imgAvatar)
    }

    private fun loadMaster() {
        setLoading(true)
        lifecycleScope.launch(Dispatchers.IO) {
            val csv = suspendCancellableCoroutine<String?> { c ->
                ApiService.fetchMasterCsv { c.resume(it) }
            }
            launch(Dispatchers.Main) {
                setLoading(false)
                if (csv != null) {
                    masterRows = CsvParser.parseMaster(csv)
                    populateBoards()
                } else {
                    Toast.makeText(this@HomeActivity,
                        getString(R.string.err_no_internet), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun populateBoards() {
        val user   = AppPrefs.getUser()
        val boards = masterRows.map { it.board }.distinct().sorted()
        spinBoard.adapter = makeAdapter(boards)
        spinBoard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(a: AdapterView<*>, v: View?, pos: Int, id: Long) = populateClasses(boards[pos])
            override fun onNothingSelected(a: AdapterView<*>) {}
        }
        val idx = boards.indexOf(user?.board ?: "")
        if (idx >= 0) spinBoard.setSelection(idx)
    }

    private fun populateClasses(board: String) {
        val user    = AppPrefs.getUser()
        val classes = masterRows.filter { it.board == board }.map { it.classVal }.distinct().sorted()
        spinClass.adapter = makeAdapter(classes)
        spinClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(a: AdapterView<*>, v: View?, pos: Int, id: Long) = populateSubjects(board, classes[pos])
            override fun onNothingSelected(a: AdapterView<*>) {}
        }
        val idx = classes.indexOf(user?.classVal ?: "")
        if (idx >= 0) spinClass.setSelection(idx)
    }

    private fun populateSubjects(board: String, cls: String) {
        val subs = masterRows.filter { it.board == board && it.classVal == cls }.map { it.subject }.distinct().sorted()
        spinSubject.adapter = makeAdapter(subs)
        spinSubject.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(a: AdapterView<*>, v: View?, pos: Int, id: Long) = populateChapters(board, cls, subs[pos])
            override fun onNothingSelected(a: AdapterView<*>) {}
        }
    }

    private fun populateChapters(board: String, cls: String, sub: String) {
        val chapters = masterRows.filter { it.board == board && it.classVal == cls && it.subject == sub }.map { it.chapter }.distinct()
        spinChapter.adapter = makeAdapter(listOf("All Chapters") + chapters)
    }

    private fun startQuiz() {
        val board   = spinBoard.selectedItem?.toString()   ?: return
        val cls     = spinClass.selectedItem?.toString()   ?: return
        val subject = spinSubject.selectedItem?.toString() ?: return
        val chapter = spinChapter.selectedItem?.toString() ?: "All Chapters"
        val diff    = spinDiff.selectedItem?.toString()    ?: "All"

        val rows = masterRows.filter {
            it.board == board && it.classVal == cls && it.subject == subject &&
            (chapter == "All Chapters" || it.chapter == chapter)
        }
        if (rows.isEmpty()) { Toast.makeText(this, "কোনো প্রশ্ন নেই!", Toast.LENGTH_SHORT).show(); return }

        Intent(this, QuizActivity::class.java).apply {
            putStringArrayListExtra("CSV_URLS", ArrayList(rows.map { it.csvlink }))
            putExtra("BOARD", board); putExtra("CLASS", cls)
            putExtra("SUBJECT", subject); putExtra("CHAPTER", chapter)
            putExtra("DIFF", diff); putExtra("QCOUNT", qCount)
            startActivity(this)
        }
    }

    private fun makeAdapter(list: List<String>) =
        ArrayAdapter(this, android.R.layout.simple_spinner_item, list).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    private fun setLoading(on: Boolean) {
        progressBar.visibility = if (on) View.VISIBLE else View.GONE
        btnStart.isEnabled     = !on
    }

    private fun bindViews() {
        spinBoard   = findViewById(R.id.spin_h_board)
        spinClass   = findViewById(R.id.spin_h_class)
        spinSubject = findViewById(R.id.spin_h_subject)
        spinChapter = findViewById(R.id.spin_h_chapter)
        spinDiff    = findViewById(R.id.spin_h_diff)
        seekQCount  = findViewById(R.id.seek_qcount)
        tvQCount    = findViewById(R.id.tv_qcount)
        btnStart    = findViewById(R.id.btn_start_quiz)
        progressBar = findViewById(R.id.home_progress)
        tvStreak    = findViewById(R.id.tv_streak)
        imgAvatar   = findViewById(R.id.img_home_avatar)
        tvName      = findViewById(R.id.tv_home_name)
        spinDiff.adapter = makeAdapter(listOf("All","Easy","Medium","Hard"))
    }
}
