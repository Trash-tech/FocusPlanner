package com.example.focusplanner

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locale = Locale.forLanguageTag("ru-RU")
        val date = SimpleDateFormat("EEEE, d MMMM", locale).format(Date())
        findViewById<TextView>(R.id.dateLabel).text = date.uppercase(locale)

        findViewById<View>(R.id.navTasks).setOnClickListener { openTasks() }
        findViewById<View>(R.id.allTasks).setOnClickListener { openTasks() }
        findViewById<View>(R.id.addTask).setOnClickListener { openTasks(addNew = true) }
        findViewById<View>(R.id.cardProjects).setOnClickListener { openTasks() }
        findViewById<View>(R.id.cardNotes).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Мои заметки")
                .setMessage("Kotlin • Коллекции и generics\nAndroid • Activity и жизненный цикл\nSQL • JOIN и подзапросы")
                .setPositiveButton("Понятно", null).show()
        }
        findViewById<View>(R.id.lessonCard).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Ближайшая пара")
                .setMessage("Разработка мобильных приложений\n16:55–18:30\nПрактика, аудитория 297")
                .setPositiveButton("Закрыть", null).show()
        }
        findViewById<View>(R.id.navProfile).setOnClickListener { showAbout() }
        refreshProgress()
    }

    override fun onResume() {
        super.onResume()
        refreshProgress()
    }

    private fun refreshProgress() {
        val tasks = TaskStore.load(this)
        val total = tasks.size
        val done = tasks.count { it.done }
        val remaining = total - done
        val percent = if (total == 0) 0 else (100 * done / total)
        findViewById<TextView>(R.id.progressText).text = "$done из $total задач"
        findViewById<TextView>(R.id.progressPercentText).text = "$percent% выполнено"
        findViewById<TextView>(R.id.progressRemainingText).text = "Осталось $remaining дела"
        val fill = findViewById<View>(R.id.progressFill)
        val rest = findViewById<View>(R.id.progressRest)
        (fill.layoutParams as LinearLayout.LayoutParams).apply { weight = done.toFloat(); fill.layoutParams = this }
        (rest.layoutParams as LinearLayout.LayoutParams).apply { weight = remaining.toFloat(); rest.layoutParams = this }
    }

    private fun openTasks(addNew: Boolean = false) {
        startActivity(Intent(this, TasksActivity::class.java).apply { putExtra("addNew", addNew) })
    }

    private fun showAbout() {
        AlertDialog.Builder(this).setTitle("Фокус • Учебный планировщик")
            .setMessage("Планируй задачи, отмечай выполненные дела и следи за прогрессом.\n\nРазработано на Kotlin + Android XML.")
            .setPositiveButton("Отлично", null).show()
    }
}
