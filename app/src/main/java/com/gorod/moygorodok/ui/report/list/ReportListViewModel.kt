package com.gorod.moygorodok.ui.report.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Report
import com.gorod.moygorodok.data.repository.ReportError
import com.gorod.moygorodok.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportListViewModel(
    private val repository: ReportRepository = ReportRepository.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        load(initial = true)
    }

    fun refresh() = load(initial = false)

    fun load(initial: Boolean) {
        viewModelScope.launch {
            if (initial) _state.value = State.Loading else _isRefreshing.value = true
            val result = repository.fetchMy(page = 1)
            _isRefreshing.value = false
            result.fold(
                onSuccess = { reports ->
                    _state.value = if (reports.isEmpty()) State.Empty else State.Data(reports)
                },
                onFailure = { throwable ->
                    val message = (throwable as? ReportError)?.message
                        ?: throwable.message
                        ?: "Не удалось загрузить жалобы"
                    _state.value = State.Error(message)
                }
            )
        }
    }

    fun removeFromList(id: Int) {
        val current = _state.value
        if (current is State.Data) {
            val filtered = current.reports.filterNot { it.id == id }
            _state.value = if (filtered.isEmpty()) State.Empty else State.Data(filtered)
        }
    }

    sealed class State {
        object Loading : State()
        object Empty : State()
        data class Data(val reports: List<Report>) : State()
        data class Error(val message: String) : State()
    }
}
