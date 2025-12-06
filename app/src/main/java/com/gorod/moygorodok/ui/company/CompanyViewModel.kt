package com.gorod.moygorodok.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.Company
import com.gorod.moygorodok.data.model.CompanyCategory
import com.gorod.moygorodok.data.model.MockCompanies

class CompanyViewModel : ViewModel() {

    private val _companies = MutableLiveData<List<Company>>()
    val companies: LiveData<List<Company>> = _companies

    private val _categories = MutableLiveData<List<CompanyCategory>>()
    val categories: LiveData<List<CompanyCategory>> = _categories

    private val _selectedCategory = MutableLiveData<CompanyCategory?>()
    val selectedCategory: LiveData<CompanyCategory?> = _selectedCategory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadData()
    }

    private fun loadData() {
        _isLoading.value = true
        _categories.value = MockCompanies.getCategories()
        _companies.value = MockCompanies.getCompanies()
        _isLoading.value = false
    }

    fun filterByCategory(category: CompanyCategory?) {
        _selectedCategory.value = category
        _companies.value = if (category == null) {
            MockCompanies.getCompanies()
        } else {
            MockCompanies.getCompaniesByCategory(category)
        }
    }

    fun refresh() {
        val currentCategory = _selectedCategory.value
        loadData()
        filterByCategory(currentCategory)
    }
}
