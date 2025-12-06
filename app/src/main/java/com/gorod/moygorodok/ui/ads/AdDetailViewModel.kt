package com.gorod.moygorodok.ui.ads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Ad
import com.gorod.moygorodok.data.repository.AdRepository
import kotlinx.coroutines.launch

class AdDetailViewModel : ViewModel() {

    private val repository = AdRepository.getInstance()

    private val _ad = MutableLiveData<Ad?>()
    val ad: LiveData<Ad?> = _ad

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun loadAd(adId: String) {
        _isLoading.value = true

        viewModelScope.launch {
            val result = repository.getAdById(adId)
            result.fold(
                onSuccess = { ad ->
                    _ad.value = ad
                    _isFavorite.value = ad.isFavorite
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Ошибка загрузки"
                    _isLoading.value = false
                }
            )
        }
    }

    fun toggleFavorite() {
        val adId = _ad.value?.id ?: return

        viewModelScope.launch {
            val result = repository.toggleFavorite(adId)
            result.onSuccess {
                _isFavorite.value = !(_isFavorite.value ?: false)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
