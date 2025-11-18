package com.gorod.moygorodok.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.News
import com.gorod.moygorodok.data.model.NewsCategory
import com.gorod.moygorodok.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsListViewModel : ViewModel() {

    private val repository = NewsRepository.getInstance()

    private val _newsList = MutableLiveData<List<News>>()
    val newsList: LiveData<List<News>> = _newsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _selectedCategory = MutableLiveData<NewsCategory?>()
    val selectedCategory: LiveData<NewsCategory?> = _selectedCategory

    init {
        loadNews()
    }

    fun loadNews() {
        _isLoading.value = true

        viewModelScope.launch {
            val category = _selectedCategory.value
            val result = if (category != null) {
                repository.getNewsByCategory(category)
            } else {
                repository.getNewsList()
            }

            result.fold(
                onSuccess = { news ->
                    _newsList.value = news
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Ошибка загрузки"
                    _isLoading.value = false
                }
            )
        }
    }

    fun refreshNews() {
        _isRefreshing.value = true

        viewModelScope.launch {
            val category = _selectedCategory.value
            val result = if (category != null) {
                repository.getNewsByCategory(category)
            } else {
                repository.refreshNews()
            }

            result.fold(
                onSuccess = { news ->
                    _newsList.value = news
                    _isRefreshing.value = false
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Ошибка обновления"
                    _isRefreshing.value = false
                }
            )
        }
    }

    fun setCategory(category: NewsCategory?) {
        if (_selectedCategory.value != category) {
            _selectedCategory.value = category
            loadNews()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
