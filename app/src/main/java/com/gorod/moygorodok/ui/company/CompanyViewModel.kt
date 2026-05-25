package com.gorod.moygorodok.ui.company

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.local.CityManager
import com.gorod.moygorodok.data.model.Company
import com.gorod.moygorodok.data.model.CompanyCategory
import com.gorod.moygorodok.data.repository.CompanyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CompanyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CompanyRepository.getInstance()
    private val cityManager = CityManager.getInstance(application)

    private val _items = MutableLiveData<List<Company>>(emptyList())
    val items: LiveData<List<Company>> = _items

    private val _categories = MutableLiveData<List<CompanyCategory>>(emptyList())
    val categories: LiveData<List<CompanyCategory>> = _categories

    private val _selectedCategoryId = MutableLiveData<Int?>(null)
    val selectedCategoryId: LiveData<Int?> = _selectedCategoryId

    private val _openNow = MutableLiveData(false)
    val openNow: LiveData<Boolean> = _openNow

    private val _searchQuery = MutableLiveData<String?>(null)
    val searchQuery: LiveData<String?> = _searchQuery

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _total = MutableLiveData(0)
    val total: LiveData<Int> = _total

    private val _cityRequired = MutableLiveData(false)
    val cityRequired: LiveData<Boolean> = _cityRequired

    private var currentPage = 1
    private var lastPage = 1
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            cityManager.selectedCityId.collectLatest {
                loadCategories()
                loadFirstPage()
            }
        }
    }

    fun refresh() {
        _isRefreshing.value = true
        loadFirstPage(silent = true)
    }

    fun setCategory(categoryId: Int?) {
        if (_selectedCategoryId.value == categoryId) return
        _selectedCategoryId.value = categoryId
        loadFirstPage()
    }

    fun setOpenNow(value: Boolean) {
        if (_openNow.value == value) return
        _openNow.value = value
        loadFirstPage()
    }

    fun setSearch(query: String) {
        val normalized = query.takeIf { it.isNotBlank() }
        if (_searchQuery.value == normalized) return
        _searchQuery.value = normalized
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

    private fun loadCategories() {
        viewModelScope.launch {
            repository.fetchCategories()
                .onSuccess { _categories.value = it }
        }
    }

    private fun loadFirstPage(silent: Boolean = false) {
        if (!silent) _isLoading.value = true
        loadPage(1, append = false)
    }

    private fun loadPage(page: Int, append: Boolean) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val cityId = cityManager.getSelectedCityIdSync()
            if (cityId == null) {
                _cityRequired.value = true
                _items.value = emptyList()
                _isLoading.value = false
                _isRefreshing.value = false
                return@launch
            }
            _cityRequired.value = false

            val result = repository.fetchList(
                cityId = cityId,
                search = _searchQuery.value,
                categoryId = _selectedCategoryId.value,
                openNow = _openNow.value == true,
                page = page
            )
            result.fold(
                onSuccess = { pageData ->
                    val newItems = if (append) {
                        _items.value.orEmpty() + pageData.items
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
