package com.gorod.moygorodok.ui.report.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Report
import com.gorod.moygorodok.data.repository.ReportError
import com.gorod.moygorodok.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportDetailViewModel(
    private val reportId: Int,
    private val repository: ReportRepository = ReportRepository.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 2)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = State.Loading
            repository.fetch(reportId).fold(
                onSuccess = { _state.value = State.Data(it) },
                onFailure = {
                    val msg = (it as? ReportError)?.message ?: it.message ?: "Ошибка загрузки"
                    _state.value = State.Error(msg)
                }
            )
        }
    }

    fun delete() {
        viewModelScope.launch {
            _isProcessing.value = true
            val result = repository.delete(reportId)
            _isProcessing.value = false
            result.fold(
                onSuccess = { _events.tryEmit(Event.Deleted) },
                onFailure = {
                    val msg = (it as? ReportError)?.message ?: it.message ?: "Не удалось удалить"
                    _events.tryEmit(Event.Error(msg))
                }
            )
        }
    }

    sealed class State {
        object Loading : State()
        data class Data(val report: Report) : State()
        data class Error(val message: String) : State()
    }

    sealed class Event {
        object Deleted : Event()
        data class Error(val message: String) : Event()
    }

    companion object {
        fun factory(reportId: Int): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReportDetailViewModel(reportId) as T
            }
        }
    }
}
