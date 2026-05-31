package com.gorod.moygorodok.ui.news.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.local.CityManager
import com.gorod.moygorodok.data.model.News
import com.gorod.moygorodok.data.repository.NewsRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewsListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NewsRepository.getInstance()
    private val cityManager = CityManager.getInstance(application)

    private val _newsList = MutableLiveData<List<News>>(emptyList())
    val newsList: LiveData<List<News>> = _newsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _isLoadingMore = MutableLiveData<Boolean>()
    val isLoadingMore: LiveData<Boolean> = _isLoadingMore

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var currentPage = 1
    private var lastPage = 1
    private var currentCityId: Int? = null

    init {
        observeCity()
    }

    private fun observeCity() {
        viewModelScope.launch {
            cityManager.selectedCityId.collectLatest { cityId ->
                currentCityId = cityId
                loadFirstPage(cityId, showSpinner = true)
            }
        }
    }

    private suspend fun loadFirstPage(cityId: Int?, showSpinner: Boolean) {
        if (showSpinner) _isLoading.value = true
        repository.getNewsList(cityId, page = 1)
            .onSuccess { page ->
                _newsList.value = page.items
                currentPage = page.currentPage
                lastPage = page.lastPage
            }
            .onFailure {
                _errorMessage.value = it.message ?: "Ошибка загрузки"
            }
        _isLoading.value = false
    }

    fun refreshNews() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val cityId = cityManager.selectedCityId.first()
            currentCityId = cityId
            repository.getNewsList(cityId, page = 1)
                .onSuccess { page ->
                    _newsList.value = page.items
                    currentPage = page.currentPage
                    lastPage = page.lastPage
                }
                .onFailure {
                    _errorMessage.value = it.message ?: "Ошибка обновления"
                }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        if (_isLoadingMore.value == true || currentPage >= lastPage) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            repository.getNewsList(currentCityId, page = currentPage + 1)
                .onSuccess { page ->
                    val combined = (_newsList.value.orEmpty()) + page.items
                    _newsList.value = combined
                    currentPage = page.currentPage
                    lastPage = page.lastPage
                }
                .onFailure {
                    _errorMessage.value = it.message ?: "Ошибка загрузки страницы"
                }
            _isLoadingMore.value = false
        }
    }

    fun loadNews() {
        viewModelScope.launch {
            loadFirstPage(currentCityId, showSpinner = true)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
