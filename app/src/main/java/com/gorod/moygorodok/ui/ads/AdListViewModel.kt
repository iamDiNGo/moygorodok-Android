package com.gorod.moygorodok.ui.ads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Ad
import com.gorod.moygorodok.data.model.AdCategory
import com.gorod.moygorodok.data.model.AdFilter
import com.gorod.moygorodok.data.model.SortOption
import com.gorod.moygorodok.data.repository.AdRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdListViewModel : ViewModel() {

    private val repository = AdRepository.getInstance()

    private val _adsList = MutableLiveData<List<Ad>>()
    val adsList: LiveData<List<Ad>> = _adsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _currentFilter = MutableLiveData(AdFilter())
    val currentFilter: LiveData<AdFilter> = _currentFilter

    private val _resultsCount = MutableLiveData<Int>()
    val resultsCount: LiveData<Int> = _resultsCount

    private var searchJob: Job? = null

    init {
        loadAds()
    }

    fun loadAds() {
        _isLoading.value = true

        viewModelScope.launch {
            val result = repository.getAds(_currentFilter.value ?: AdFilter())
            result.fold(
                onSuccess = { ads ->
                    _adsList.value = ads
                    _resultsCount.value = ads.size
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Ошибка загрузки"
                    _isLoading.value = false
                }
            )
        }
    }

    fun refreshAds() {
        _isRefreshing.value = true

        viewModelScope.launch {
            val result = repository.refreshAds(_currentFilter.value ?: AdFilter())
            result.fold(
                onSuccess = { ads ->
                    _adsList.value = ads
                    _resultsCount.value = ads.size
                    _isRefreshing.value = false
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Ошибка обновления"
                    _isRefreshing.value = false
                }
            )
        }
    }

    fun setCategory(category: AdCategory?) {
        val current = _currentFilter.value ?: AdFilter()
        if (current.category != category) {
            _currentFilter.value = current.copy(category = category)
            loadAds()
        }
    }

    fun setSortOption(sortOption: SortOption) {
        val current = _currentFilter.value ?: AdFilter()
        if (current.sortOption != sortOption) {
            _currentFilter.value = current.copy(sortOption = sortOption)
            loadAds()
        }
    }

    fun setPriceRange(minPrice: Double?, maxPrice: Double?) {
        val current = _currentFilter.value ?: AdFilter()
        _currentFilter.value = current.copy(minPrice = minPrice, maxPrice = maxPrice)
        loadAds()
    }

    fun setSearchQuery(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            val current = _currentFilter.value ?: AdFilter()
            _currentFilter.value = current.copy(searchQuery = query.takeIf { it.isNotBlank() })
            loadAds()
        }
    }

    fun clearFilters() {
        _currentFilter.value = AdFilter()
        loadAds()
    }

    fun hasActiveFilters(): Boolean {
        val filter = _currentFilter.value ?: return false
        return filter.category != null ||
                filter.minPrice != null ||
                filter.maxPrice != null ||
                !filter.searchQuery.isNullOrBlank()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
