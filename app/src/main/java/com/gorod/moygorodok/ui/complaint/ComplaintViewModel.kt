package com.gorod.moygorodok.ui.complaint

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.ComplaintCategory
import com.gorod.moygorodok.data.model.MockComplaints

class ComplaintViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<ComplaintCategory>>()
    val categories: LiveData<List<ComplaintCategory>> = _categories

    private val _selectedCategory = MutableLiveData<ComplaintCategory?>()
    val selectedCategory: LiveData<ComplaintCategory?> = _selectedCategory

    private val _attachedImages = MutableLiveData<List<String>>(emptyList())
    val attachedImages: LiveData<List<String>> = _attachedImages

    private val _submitSuccess = MutableLiveData<Boolean>()
    val submitSuccess: LiveData<Boolean> = _submitSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _categories.value = MockComplaints.getCategories()
    }

    fun selectCategory(category: ComplaintCategory) {
        _selectedCategory.value = category
    }

    fun addImage(imageUri: String) {
        val currentImages = _attachedImages.value?.toMutableList() ?: mutableListOf()
        if (currentImages.size < 5) {
            currentImages.add(imageUri)
            _attachedImages.value = currentImages
        }
    }

    fun removeImage(position: Int) {
        val currentImages = _attachedImages.value?.toMutableList() ?: mutableListOf()
        if (position in currentImages.indices) {
            currentImages.removeAt(position)
            _attachedImages.value = currentImages
        }
    }

    fun submitComplaint(
        title: String,
        description: String,
        address: String,
        authorName: String,
        authorPhone: String
    ): Boolean {
        val category = _selectedCategory.value ?: return false

        if (title.isBlank() || description.isBlank() || address.isBlank()) {
            return false
        }

        _isLoading.value = true

        // Simulate API call
        _isLoading.postValue(false)
        _submitSuccess.postValue(true)

        return true
    }

    fun clearForm() {
        _selectedCategory.value = null
        _attachedImages.value = emptyList()
        _submitSuccess.value = false
    }
}
