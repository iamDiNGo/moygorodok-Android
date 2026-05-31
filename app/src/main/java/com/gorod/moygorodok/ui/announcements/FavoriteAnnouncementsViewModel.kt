package com.gorod.moygorodok.ui.announcements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.Announcement
import com.gorod.moygorodok.data.repository.AnnouncementRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FavoriteAnnouncementsViewModel : ViewModel() {

    private val repository = AnnouncementRepository.getInstance()

    private val _items = MutableLiveData<List<Announcement>>(emptyList())
    val items: LiveData<List<Announcement>> = _items

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var currentPage = 1
    private var lastPage = 1
    private var loadJob: Job? = null

    fun refresh() {
        _isRefreshing.value = true
        loadFirstPage(silent = true)
    }

    fun loadFirstPage(silent: Boolean = false) {
        if (!silent) _isLoading.value = true
        loadPage(1, append = false)
    }

    fun loadMoreIfNeeded(lastVisible: Int) {
        val items = _items.value ?: return
        if (_isLoading.value == true) return
        if (currentPage >= lastPage) return
        if (lastVisible < items.size - 3) return
        loadPage(currentPage + 1, append = true)
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun loadPage(page: Int, append: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val result = repository.fetchFavorites(page = page)
            result.fold(
                onSuccess = { pageData ->
                    _items.value = if (append) {
                        (_items.value.orEmpty()) + pageData.items
                    } else {
                        pageData.items
                    }
                    currentPage = pageData.currentPage
                    lastPage = pageData.lastPage
                },
                onFailure = { _errorMessage.value = it.message }
            )
            _isLoading.value = false
            _isRefreshing.value = false
        }
    }
}
