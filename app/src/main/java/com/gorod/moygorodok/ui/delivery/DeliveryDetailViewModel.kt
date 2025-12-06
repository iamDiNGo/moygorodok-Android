package com.gorod.moygorodok.ui.delivery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Delivery
import com.gorod.moygorodok.data.repository.DeliveryRepository
import kotlinx.coroutines.launch

class DeliveryDetailViewModel : ViewModel() {

    private val repository = DeliveryRepository()

    private val _delivery = MutableLiveData<Delivery?>()
    val delivery: LiveData<Delivery?> = _delivery

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _selectedCategoryIndex = MutableLiveData<Int>()
    val selectedCategoryIndex: LiveData<Int> = _selectedCategoryIndex

    fun loadDelivery(deliveryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getDeliveryById(deliveryId)
                _delivery.value = result
                _isFavorite.value = result?.isFavorite ?: false
                _selectedCategoryIndex.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(index: Int) {
        _selectedCategoryIndex.value = index
    }

    fun toggleFavorite() {
        _isFavorite.value = !(_isFavorite.value ?: false)
    }
}
