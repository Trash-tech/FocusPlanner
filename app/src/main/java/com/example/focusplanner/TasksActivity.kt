package com.example.focusplanner

import android.app.Activity
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class TasksActivity : Activity() {
    private lateinit var tasks: MutableList<TaskItem>
    private lateinit var listContainer: LinearLayout
    private var filter = 0 // 0 — все, 1 — в процессе, 2 — готово

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        tasks = TaskStore.load(this)
        listContainer = findViewById(R.id.taskContainer)

        findViewById<View>(R.id.tasksBack).setOnClickListener { finish() }
        findViewById<View>(R.id.navHomeFromTasks).setOnClickListener { finish() }
        findViewById<View>(R.id.filterAll).setOnClickListener { changeFilter(0) }
        findViewById<View>(R.id.filterActive).setOnClickListener { changeFilter(1) }
        findViewById<View>(R.id.filterDone).setOnClickListener { changeFilter(2) }
        findViewById<View>(R.id.createTaskButton).setOnClickListener { askNewTask() }
        findViewById<View>(R.id.tasksNavProfile).setOnClickListener {
            AlertDialog.Builder(this).setTitle("Фокус")
                .setMessage("Мой персональный планировщик задач • Kotlin + XML")
                .setPositiveButton("Закрыть", null).show()
        }
        updateScreen()

        if (savedInstanceState == null && intent.getBooleanExtra("addNew", false)) {
            askNewTask()
        }
    }

    private fun changeFilter(newFilter: Int) {
        filter = newFilter
        updateScreen()
    }

    private fun updateScreen() {
        val total = tasks.size
        val done = tasks.count { it.done }
        val rest = total - done
        val percent = if (total == 0) 0 else (100 * done / total)
        findViewById<TextView>(R.id.tasksDoneValue).text = "$done / $total"
        findViewById<TextView>(R.id.tasksPercentValue).text = "$percent%"
        findViewById<TextView>(R.id.tasksCountLabel).text = "$total задач"
        findViewById<TextView>(R.id.tasksDoneLabel).text = "Задач выполнено"
        val fill = findViewById<View>(R.id.tasksProgressFill)
        val remaining = findViewById<View>(R.id.tasksProgressRest)
        (fill.layoutParams as LinearLayout.LayoutParams).apply { weight = done.toFloat(); fill.layoutParams = this }
        (remaining.layoutParams as LinearLayout.LayoutParams).apply { weight = rest.toFloat(); remaining.layoutParams = this }

        val filters = intArrayOf(R.id.filterAll, R.id.filterActive, R.id.filterDone)
        filters.forEachIndexed { index, id ->
            findViewById<TextView>(id).apply {
                setBackgroundResource(if (index == filter) R.drawable.fp_pill_selected else R.drawable.fp_pill_unselected)
                setTextColor(Color.parseColor(if (index == filter) "#FFFFFF" else "#6E748B"))
            }
        }

        listContainer.removeAllViews()
        val visible = tasks.filter { filter == 0 || (filter == 1 && !it.done) || (filter == 2 && it.done) }
        if (visible.isEmpty()) {
            val empty = TextView(this).apply {
                text = "Здесь пока нет задач ✨"
                textSize = 15f
                gravity = Gravity.CENTER
                setTextColor(Color.parseColor("#8790A3"))
                setPadding(dp(16), dp(30), dp(16), dp(30))
            }
            listContainer.addView(empty)
        } else visible.forEach { makeRow(it) }
    }

    private fun makeRow(task: TaskItem) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(15), dp(14), dp(15), dp(14))
            setBackgroundResource(R.drawable.fp_card)
            isClickable = true
            isFocusable = true
        }
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lp.topMargin = dp(10)
        listContainer.addView(row, lp)

        val check = TextView(this).apply {
            text = if (task.done) "✓" else ""
            gravity = Gravity.CENTER
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            setBackgroundResource(if (task.done) R.drawable.fp_task_done else R.drawable.fp_task_undone)
        }
        row.addView(check, LinearLayout.LayoutParams(dp(32), dp(32)))

        val column = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val columnLp = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        columnLp.marginStart = dp(13)
        row.addView(column, columnLp)

        val title = TextView(this).apply {
            text = task.title
            textSize = 14.5f
            setTextColor(Color.parseColor(if (task.done) "#9298AA" else "#1C2341"))
            setTypeface(null, Typeface.BOLD)
            maxLines = 2
            if (task.done) paintFlags = paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        }
        column.addView(title)
        val category = TextView(this).apply {
            text = task.category
            textSize = 11.5f
            setTextColor(Color.parseColor("#8790A3"))
            setPadding(0, dp(5), 0, 0)
        }
        column.addView(category)

        val deadline = TextView(this).apply {
            text = if (task.done) "Готово" else task.deadline
            textSize = 10.5f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor(if (task.done) "#26946C" else "#655FE2"))
            setBackgroundResource(if (task.done) R.drawable.fp_pill_green else R.drawable.fp_pill_purple)
            setPadding(dp(9), dp(7), dp(9), dp(7))
            gravity = Gravity.CENTER
        }
        val deadlineLp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        deadlineLp.marginStart = dp(7)
        row.addView(deadline, deadlineLp)

        row.setOnClickListener {
            task.done = !task.done
            TaskStore.save(this, tasks)
            updateScreen()
        }
    }

    private fun askNewTask() {
        val input = EditText(this).apply {
            hint = "Например, сделать домашнее задание"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setSingleLine(true)
            setTextColor(Color.parseColor("#1C2341"))
        }
        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(4), dp(20), 0)
            addView(input)
        }
        AlertDialog.Builder(this)
            .setTitle("Новая задача ✨")
            .setMessage("Добавь дело в свой план")
            .setView(wrapper)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Добавить") { _, _ ->
                val title = input.text.toString().trim()
                if (title.isNotEmpty()) {
                    tasks.add(0, TaskStore.makeTask(title))
                    TaskStore.save(this, tasks)
                    filter = 0
                    updateScreen()
                    Toast.makeText(this, "Задача добавлена", Toast.LENGTH_SHORT).show()
                } else Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show()
            }.show()
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density + 0.5f).toInt()
}
