package com.gorod.moygorodok.ui.emergency

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.local.CityManager
import com.gorod.moygorodok.data.model.EmergencyCategory
import com.gorod.moygorodok.data.model.EmergencyContact
import com.gorod.moygorodok.data.repository.EmergencyRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EmergencyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EmergencyRepository.getInstance()
    private val cityManager = CityManager.getInstance(application)

    private val _contacts = MutableLiveData<List<EmergencyContact>>(emptyList())
    val contacts: LiveData<List<EmergencyContact>> = _contacts

    private val _mainContacts = MutableLiveData<List<EmergencyContact>>(emptyList())
    val mainContacts: LiveData<List<EmergencyContact>> = _mainContacts

    private val _groupedContacts = MutableLiveData<List<Pair<EmergencyCategory, List<EmergencyContact>>>>(emptyList())
    val groupedContacts: LiveData<List<Pair<EmergencyCategory, List<EmergencyContact>>>> = _groupedContacts

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        viewModelScope.launch {
            cityManager.selectedCityId.collectLatest { cityId ->
                loadContacts(cityId)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            loadContacts(cityManager.selectedCityId.first())
        }
    }

    private suspend fun loadContacts(cityId: Int?) {
        _isLoading.value = true
        _error.value = null
        repository.getContacts(cityId)
            .onSuccess { list ->
                _contacts.value = list
                _mainContacts.value = list.filter { it.priority > 0 }.sortedByDescending { it.priority }
                _groupedContacts.value = list
                    .groupBy { it.category }
                    .toList()
                    .sortedBy { it.first.order }
            }
            .onFailure {
                _contacts.value = emptyList()
                _mainContacts.value = emptyList()
                _groupedContacts.value = emptyList()
                _error.value = it.message ?: "Ошибка загрузки"
            }
        _isLoading.value = false
    }
}
