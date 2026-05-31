package com.gorod.moygorodok.ui.announcements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Announcement
import com.gorod.moygorodok.data.repository.AnnouncementRepository
import kotlinx.coroutines.launch

class AnnouncementDetailViewModel : ViewModel() {

    private val repository = AnnouncementRepository.getInstance()

    private val _announcement = MutableLiveData<Announcement?>()
    val announcement: LiveData<Announcement?> = _announcement

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMutating = MutableLiveData(false)
    val isMutating: LiveData<Boolean> = _isMutating

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _closed = MutableLiveData(false)
    val closed: LiveData<Boolean> = _closed

    private var announcementId: Int? = null

    fun load(id: Int) {
        announcementId = id
        _isLoading.value = true
        viewModelScope.launch {
            repository.fetchDetail(id).fold(
                onSuccess = { _announcement.value = it },
                onFailure = { _errorMessage.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun refresh() {
        announcementId?.let { load(it) }
    }

    fun toggleFavorite() {
        val current = _announcement.value ?: return
        if (_isMutating.value == true) return
        _isMutating.value = true
        viewModelScope.launch {
            val result = if (current.isFavorite) {
                repository.removeFavorite(current.id).map { false }
            } else {
                repository.addFavorite(current.id).map { it }
            }
            result.fold(
                onSuccess = { newState ->
                    _announcement.value = current.copy(isFavorite = newState)
                },
                onFailure = { _errorMessage.value = it.message }
            )
            _isMutating.value = false
        }
    }

    fun close() {
        val current = _announcement.value ?: return
        if (_isMutating.value == true) return
        _isMutating.value = true
        viewModelScope.launch {
            repository.close(current.id).fold(
                onSuccess = { _announcement.value = it },
                onFailure = { _errorMessage.value = it.message }
            )
            _isMutating.value = false
        }
    }

    fun renew() {
        val current = _announcement.value ?: return
        if (_isMutating.value == true) return
        _isMutating.value = true
        viewModelScope.launch {
            repository.renew(current.id).fold(
                onSuccess = { _announcement.value = it },
                onFailure = { _errorMessage.value = it.message }
            )
            _isMutating.value = false
        }
    }

    fun delete() {
        val current = _announcement.value ?: return
        if (_isMutating.value == true) return
        _isMutating.value = true
        viewModelScope.launch {
            repository.delete(current.id).fold(
                onSuccess = { _closed.value = true },
                onFailure = { _errorMessage.value = it.message }
            )
            _isMutating.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
