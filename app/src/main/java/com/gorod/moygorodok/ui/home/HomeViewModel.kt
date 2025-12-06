package com.gorod.moygorodok.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.HomeWidget
import com.gorod.moygorodok.data.model.MockHomeWidgets
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _widgets = MutableLiveData<List<HomeWidget>>()
    val widgets: LiveData<List<HomeWidget>> = _widgets

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadWidgets()
    }

    fun loadWidgets() {
        viewModelScope.launch {
            _isLoading.value = true
            delay(300) // Simulate loading
            _widgets.value = MockHomeWidgets.getWidgets()
            _isLoading.value = false
        }
    }

    fun refresh() {
        loadWidgets()
    }
}