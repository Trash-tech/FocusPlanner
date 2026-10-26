package com.example.focusplanner

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class TaskItem(
    val id: String,
    val title: String,
    val category: String,
    val deadline: String,
    var done: Boolean
)

object TaskStore {
    private const val PREFS = "focus_data"
    private const val KEY = "tasks_v1"

    fun load(context: Context): MutableList<TaskItem> {
        val preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = preferences.getString(KEY, null) ?: return seed()
        return try {
            val list = mutableListOf<TaskItem>()
            val arr = JSONArray(json)
            for (index in 0 until arr.length()) {
                val task = arr.getJSONObject(index)
                list.add(TaskItem(
                    task.optString("id"), task.optString("title"),
                    task.optString("category"), task.optString("deadline"),
                    task.optBoolean("done")
                ))
            }
            list
        } catch (_: Exception) { seed() }
    }

    fun save(context: Context, tasks: List<TaskItem>) {
        val arr = JSONArray()
        tasks.forEach { task ->
            arr.put(JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("category", task.category)
                put("deadline", task.deadline)
                put("done", task.done)
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY, arr.toString()).apply()
    }

    fun makeTask(title: String): TaskItem =
        TaskItem(UUID.randomUUID().toString(), title, "Личное", "Без срока", false)

    private fun seed() = mutableListOf(
        TaskItem("1", "Сдать лабораторную №6", "МиСПИСиТ", "20:00", false),
        TaskItem("2", "Подготовить презентацию", "Проектирование", "Завтра", false),
        TaskItem("3", "Повторить Kotlin: коллекции", "Самостоятельно", "Пятница", false),
        TaskItem("4", "Прочитать главу по ОС", "ОС", "Готово", true),
        TaskItem("5", "Проверить расписание", "Учёба", "Готово", true),
        TaskItem("6", "Составить план недели", "Планирование", "Готово", true),
        TaskItem("7", "Подготовить конспект", "Теория информации", "Готово", true),
        TaskItem("8", "Разобрать заметки", "Заметки", "Готово", true),
        TaskItem("9", "Оформить отчёт", "OC", "Готово", true),
        TaskItem("10", "Сделать упражнения", "Kotlin", "Готово", true)
    )
}
