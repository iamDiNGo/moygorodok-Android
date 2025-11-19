package com.gorod.moygorodok.ui.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.Currency
import com.gorod.moygorodok.data.model.MockCurrencies

class CurrencyViewModel : ViewModel() {

    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>> = _currencies

    private val _lastUpdate = MutableLiveData<String>()
    val lastUpdate: LiveData<String> = _lastUpdate

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        _isLoading.value = true
        _currencies.value = MockCurrencies.getCurrencies()
        _lastUpdate.value = MockCurrencies.getLastUpdate()
        _isLoading.value = false
    }

    fun refresh() {
        loadCurrencies()
    }
}
