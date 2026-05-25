package com.gorod.moygorodok.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.CompanyDetail
import com.gorod.moygorodok.data.repository.CompanyRepository
import kotlinx.coroutines.launch

class CompanyDetailViewModel : ViewModel() {

    private val repository = CompanyRepository.getInstance()

    private val _company = MutableLiveData<CompanyDetail?>()
    val company: LiveData<CompanyDetail?> = _company

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadCompany(companyId: Int) {
        if (companyId <= 0) return
        viewModelScope.launch {
            _isLoading.value = true
            repository.fetchDetail(companyId).fold(
                onSuccess = { _company.value = it },
                onFailure = { _errorMessage.value = it.message }
            )
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
