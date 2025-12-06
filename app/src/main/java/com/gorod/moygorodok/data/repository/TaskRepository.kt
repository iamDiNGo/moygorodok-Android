package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.model.MockTasks
import com.gorod.moygorodok.data.model.Task
import com.gorod.moygorodok.data.model.TaskCategory

class TaskRepository {

    fun getTasks(): List<Task> = MockTasks.getTasks()

    fun getTaskById(id: String): Task? = MockTasks.getTaskById(id)

    fun getTasksByCategory(category: TaskCategory): List<Task> =
        MockTasks.getTasksByCategory(category)

    fun searchTasks(query: String): List<Task> = MockTasks.searchTasks(query)

    fun filterTasks(
        tasks: List<Task>,
        category: TaskCategory? = null,
        query: String = ""
    ): List<Task> {
        var result = tasks

        if (category != null) {
            result = result.filter { it.category == category }
        }

        if (query.isNotBlank()) {
            result = result.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.location.contains(query, ignoreCase = true)
            }
        }

        return result
    }
}
