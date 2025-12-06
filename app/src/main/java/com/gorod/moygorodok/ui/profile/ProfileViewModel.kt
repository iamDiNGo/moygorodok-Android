package com.gorod.moygorodok.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.User
import com.gorod.moygorodok.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = AuthRepository.getInstance()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _firstNameError = MutableLiveData<String?>()
    val firstNameError: LiveData<String?> = _firstNameError

    private val _lastNameError = MutableLiveData<String?>()
    val lastNameError: LiveData<String?> = _lastNameError

    init {
        loadUser()
    }

    fun loadUser() {
        _user.value = repository.getCurrentUser()
    }

    fun updateProfile(firstName: String, lastName: String, email: String?) {
        if (!validateInput(firstName, lastName)) {
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            val result = repository.updateProfile(firstName, lastName, email)
            result.fold(
                onSuccess = { updatedUser ->
                    _user.value = updatedUser
                    _saveSuccess.value = true
                    _isLoading.value = false
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Ошибка сохранения"
                    _isLoading.value = false
                }
            )
        }
    }

    private fun validateInput(firstName: String, lastName: String): Boolean {
        var isValid = true

        if (firstName.isBlank()) {
            _firstNameError.value = "Введите имя"
            isValid = false
        } else {
            _firstNameError.value = null
        }

        if (lastName.isBlank()) {
            _lastNameError.value = "Введите фамилию"
            isValid = false
        } else {
            _lastNameError.value = null
        }

        return isValid
    }

    fun logout() {
        repository.logout()
    }

    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
