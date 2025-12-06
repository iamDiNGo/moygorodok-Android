package com.gorod.moygorodok.ui.delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Delivery
import com.gorod.moygorodok.data.model.DeliveryCategory
import com.gorod.moygorodok.data.repository.DeliveryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DeliveryListViewModel : ViewModel() {

    private val repository = DeliveryRepository()

    private val _deliveries = MutableLiveData<List<Delivery>>()
    val deliveries: LiveData<List<Delivery>> = _deliveries

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _selectedCategory = MutableLiveData<DeliveryCategory?>()
    val selectedCategory: LiveData<DeliveryCategory?> = _selectedCategory

    private var searchJob: Job? = null

    init {
        loadDeliveries()
    }

    fun loadDeliveries() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allDeliveries = repository.getAllDeliveries()
                val category = _selectedCategory.value

                _deliveries.value = if (category != null) {
                    allDeliveries.filter { it.category == category }
                } else {
                    allDeliveries
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCategory(category: DeliveryCategory?) {
        _selectedCategory.value = category
        loadDeliveries()
    }

    fun searchDeliveries(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            _isLoading.value = true
            try {
                if (query.isBlank()) {
                    loadDeliveries()
                } else {
                    _deliveries.value = repository.searchDeliveries(query)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadDeliveries()
    }
}
