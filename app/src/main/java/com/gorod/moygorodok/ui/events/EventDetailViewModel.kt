package com.gorod.moygorodok.ui.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Event
import com.gorod.moygorodok.data.repository.EventRepository
import kotlinx.coroutines.launch

class EventDetailViewModel : ViewModel() {

    private val repository = EventRepository()

    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getEventById(eventId)
                _event.value = result
                _isFavorite.value = result?.isFavorite ?: false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        _isFavorite.value = !(_isFavorite.value ?: false)
    }
}
