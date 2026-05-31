package com.gorod.moygorodok.ui.announcements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.local.CityManager
import com.gorod.moygorodok.data.model.Announcement
import com.gorod.moygorodok.data.model.AnnouncementCategory
import com.gorod.moygorodok.data.model.AnnouncementFilter
import com.gorod.moygorodok.data.model.AnnouncementSortOption
import com.gorod.moygorodok.data.repository.AnnouncementRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AnnouncementListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AnnouncementRepository.getInstance()
    private val cityManager = CityManager.getInstance(application)

    private val _items = MutableLiveData<List<Announcement>>(emptyList())
    val items: LiveData<List<Announcement>> = _items

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _filter = MutableLiveData(AnnouncementFilter())
    val filter: LiveData<AnnouncementFilter> = _filter

    private val _total = MutableLiveData(0)
    val total: LiveData<Int> = _total

    private var currentPage = 1
    private var lastPage = 1
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            cityManager.selectedCityId.collectLatest { _ ->
                loadFirstPage()
            }
        }
    }

    fun refresh() {
        _isRefreshing.value = true
        loadFirstPage(silent = true)
    }

    fun setCategory(category: AnnouncementCategory?) {
        val current = _filter.value ?: AnnouncementFilter()
        if (current.category == category) return
        _filter.value = current.copy(category = category)
        loadFirstPage()
    }

    fun setSort(sort: AnnouncementSortOption) {
        val current = _filter.value ?: AnnouncementFilter()
        if (current.sort == sort) return
        _filter.value = current.copy(sort = sort)
        loadFirstPage()
    }

    fun setSearch(query: String) {
        val current = _filter.value ?: AnnouncementFilter()
        val normalized = query.takeIf { it.isNotBlank() }
        if (current.search == normalized) return
        _filter.value = current.copy(search = normalized)
        loadFirstPage()
    }

    fun setPriceRange(min: Double?, max: Double?) {
        val current = _filter.value ?: AnnouncementFilter()
        _filter.value = current.copy(minPrice = min, maxPrice = max)
        loadFirstPage()
    }

    fun clearFilters() {
        _filter.value = AnnouncementFilter()
        loadFirstPage()
    }

    fun loadMoreIfNeeded(lastVisiblePosition: Int) {
        val items = _items.value ?: return
        if (_isLoading.value == true) return
        if (currentPage >= lastPage) return
        if (lastVisiblePosition < items.size - 3) return
        loadPage(currentPage + 1, append = true)
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun loadFirstPage(silent: Boolean = false) {
        if (!silent) _isLoading.value = true
        loadPage(1, append = false)
    }

    private fun loadPage(page: Int, append: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val cityId = cityManager.getSelectedCityIdSync()
            val filter = _filter.value ?: AnnouncementFilter()
            val result = repository.fetchList(cityId = cityId, filter = filter, page = page)
            result.fold(
                onSuccess = { pageData ->
                    val newItems = if (append) {
                        (_items.value.orEmpty()) + pageData.items
                    } else {
                        pageData.items
                    }
                    _items.value = newItems
                    currentPage = pageData.currentPage
                    lastPage = pageData.lastPage
                    _total.value = pageData.total
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
            _isLoading.value = false
            _isRefreshing.value = false
        }
    }
}
