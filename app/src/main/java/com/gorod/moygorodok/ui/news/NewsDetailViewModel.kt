package com.gorod.moygorodok.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.News
import com.gorod.moygorodok.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsDetailViewModel : ViewModel() {

    private val repository = NewsRepository.getInstance()

    private val _news = MutableLiveData<News?>()
    val news: LiveData<News?> = _news

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadNews(newsId: String) {
        _isLoading.value = true

        viewModelScope.launch {
            val result = repository.getNewsById(newsId)
            result.fold(
                onSuccess = { news ->
                    _news.value = news
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Ошибка загрузки"
                    _isLoading.value = false
                }
            )
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
