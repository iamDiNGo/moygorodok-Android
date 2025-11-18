package com.gorod.moygorodok.ui.emergency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.EmergencyCategory
import com.gorod.moygorodok.data.model.EmergencyContact
import com.gorod.moygorodok.data.model.MockEmergencyContacts

class EmergencyViewModel : ViewModel() {

    private val _contacts = MutableLiveData<List<EmergencyContact>>()
    val contacts: LiveData<List<EmergencyContact>> = _contacts

    private val _mainContacts = MutableLiveData<List<EmergencyContact>>()
    val mainContacts: LiveData<List<EmergencyContact>> = _mainContacts

    private val _groupedContacts = MutableLiveData<Map<EmergencyCategory, List<EmergencyContact>>>()
    val groupedContacts: LiveData<Map<EmergencyCategory, List<EmergencyContact>>> = _groupedContacts

    init {
        loadContacts()
    }

    private fun loadContacts() {
        val allContacts = MockEmergencyContacts.getContacts()
        _contacts.value = allContacts
        _mainContacts.value = MockEmergencyContacts.getMainContacts()

        // Group by category
        _groupedContacts.value = allContacts
            .groupBy { it.category }
            .toSortedMap(compareBy { it.order })
    }
}
