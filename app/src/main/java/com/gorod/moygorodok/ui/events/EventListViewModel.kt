package com.gorod.moygorodok.ui.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Event
import com.gorod.moygorodok.data.model.EventCategory
import com.gorod.moygorodok.data.repository.EventRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EventListViewModel : ViewModel() {

    private val repository = EventRepository()

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _featuredEvents = MutableLiveData<List<Event>>()
    val featuredEvents: LiveData<List<Event>> = _featuredEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _selectedCategory = MutableLiveData<EventCategory?>()
    val selectedCategory: LiveData<EventCategory?> = _selectedCategory

    private var searchJob: Job? = null

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allEvents = repository.getAllEvents()
                val category = _selectedCategory.value

                _events.value = if (category != null) {
                    allEvents.filter { it.category == category }
                } else {
                    allEvents
                }

                _featuredEvents.value = allEvents.filter { it.isFeatured }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCategory(category: EventCategory?) {
        _selectedCategory.value = category
        loadEvents()
    }

    fun searchEvents(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            _isLoading.value = true
            try {
                if (query.isBlank()) {
                    loadEvents()
                } else {
                    _events.value = repository.searchEvents(query)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadEvents()
    }
}
