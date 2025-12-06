package com.gorod.moygorodok.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.Company
import com.gorod.moygorodok.data.model.MockCompanies

class CompanyDetailViewModel : ViewModel() {

    private val _company = MutableLiveData<Company>()
    val company: LiveData<Company> = _company

    fun loadCompany(companyId: String) {
        MockCompanies.getCompanyById(companyId)?.let { company ->
            _company.value = company
        }
    }
}
