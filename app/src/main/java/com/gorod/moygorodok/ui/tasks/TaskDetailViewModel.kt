package com.gorod.moygorodok.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.Task
import com.gorod.moygorodok.data.repository.TaskRepository

class TaskDetailViewModel : ViewModel() {

    private val repository = TaskRepository()

    private val _task = MutableLiveData<Task?>()
    val task: LiveData<Task?> = _task

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun loadTask(taskId: String) {
        val task = repository.getTaskById(taskId)
        _task.value = task
        _isFavorite.value = task?.isFavorite ?: false
    }

    fun toggleFavorite() {
        _isFavorite.value = !(_isFavorite.value ?: false)
    }
}
