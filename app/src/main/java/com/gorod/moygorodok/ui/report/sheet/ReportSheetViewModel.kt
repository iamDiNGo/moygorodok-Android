package com.gorod.moygorodok.ui.report.sheet

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Report
import com.gorod.moygorodok.data.model.ReportDraft
import com.gorod.moygorodok.data.model.ReportReason
import com.gorod.moygorodok.data.model.ReportableType
import com.gorod.moygorodok.data.repository.ReportError
import com.gorod.moygorodok.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportSheetViewModel(
    private val reportableType: ReportableType,
    private val reportableId: Int,
    val reportableTitle: String,
    private val repository: ReportRepository = ReportRepository.getInstance()
) : ViewModel() {

    val reasons: List<ReportReason> = ReportReason.applicableTo(reportableType)

    private val _selectedReason = MutableStateFlow<ReportReason?>(null)
    val selectedReason: StateFlow<ReportReason?> = _selectedReason.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    private val _screenshots = MutableStateFlow<List<Uri>>(emptyList())
    val screenshots: StateFlow<List<Uri>> = _screenshots.asStateFlow()

    private val _state = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val state: StateFlow<SubmitState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 4)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun selectReason(reason: ReportReason) {
        _selectedReason.value = reason
    }

    fun setComment(value: String) {
        _comment.value = value
    }

    fun addScreenshots(uris: List<Uri>) {
        if (uris.isEmpty()) return
        val current = _screenshots.value
        val merged = (current + uris).distinct().take(MAX_PHOTOS)
        _screenshots.value = merged
    }

    fun removeScreenshot(position: Int) {
        val list = _screenshots.value.toMutableList()
        if (position in list.indices) {
            list.removeAt(position)
            _screenshots.value = list
        }
    }

    fun canSubmit(): Boolean {
        val reason = _selectedReason.value ?: return false
        if (reason.requiresComment() && _comment.value.isBlank()) return false
        return _state.value !is SubmitState.Loading
    }

    val canAddMoreScreenshots: Boolean
        get() = _screenshots.value.size < MAX_PHOTOS

    fun submit(context: Context) {
        if (!canSubmit()) return
        val draft = ReportDraft(
            reportableType = reportableType,
            reportableId = reportableId,
            reportableTitle = reportableTitle,
            reason = _selectedReason.value,
            comment = _comment.value,
            screenshots = _screenshots.value
        )

        viewModelScope.launch {
            _state.value = SubmitState.Loading
            val result = repository.submit(context.applicationContext, draft)
            result.fold(
                onSuccess = {
                    _state.value = SubmitState.Success(it)
                    _events.tryEmit(Event.Submitted)
                },
                onFailure = { throwable ->
                    val message = (throwable as? ReportError)?.message
                        ?: throwable.message
                        ?: "Не удалось отправить жалобу"
                    _state.value = SubmitState.Idle
                    _events.tryEmit(Event.Error(throwable, message))
                }
            )
        }
    }

    sealed class SubmitState {
        object Idle : SubmitState()
        object Loading : SubmitState()
        data class Success(val report: Report) : SubmitState()
    }

    sealed class Event {
        object Submitted : Event()
        data class Error(val throwable: Throwable, val message: String) : Event()
    }

    companion object {
        const val MAX_PHOTOS = 3

        fun factory(
            reportableType: ReportableType,
            reportableId: Int,
            reportableTitle: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ReportSheetViewModel(reportableType, reportableId, reportableTitle) as T
            }
        }
    }
}
