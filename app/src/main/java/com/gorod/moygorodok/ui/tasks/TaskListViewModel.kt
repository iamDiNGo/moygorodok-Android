package com.gorod.moygorodok.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Task
import com.gorod.moygorodok.data.model.TaskCategory
import com.gorod.moygorodok.data.repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {

    private val repository = TaskRepository()

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _selectedCategory = MutableLiveData<TaskCategory?>()
    val selectedCategory: LiveData<TaskCategory?> = _selectedCategory

    private var allTasks: List<Task> = emptyList()
    private var currentQuery: String = ""
    private var searchJob: Job? = null

    init {
        loadTasks()
    }

    fun loadTasks() {
        _isLoading.value = true
        viewModelScope.launch {
            delay(300)
            allTasks = repository.getTasks()
            filterTasks()
            _isLoading.value = false
        }
    }

    fun setCategory(category: TaskCategory?) {
        _selectedCategory.value = category
        filterTasks()
    }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            currentQuery = query
            filterTasks()
        }
    }

    private fun filterTasks() {
        var result = allTasks

        _selectedCategory.value?.let { category ->
            result = result.filter { it.category == category }
        }

        if (currentQuery.isNotBlank()) {
            result = result.filter {
                it.title.contains(currentQuery, ignoreCase = true) ||
                        it.description.contains(currentQuery, ignoreCase = true) ||
                        it.location.contains(currentQuery, ignoreCase = true)
            }
        }

        _tasks.value = result
    }

    fun refresh() {
        loadTasks()
    }
}
