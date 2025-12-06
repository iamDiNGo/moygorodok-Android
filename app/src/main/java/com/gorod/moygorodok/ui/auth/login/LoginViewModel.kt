package com.gorod.moygorodok.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.AuthState
import com.gorod.moygorodok.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository.getInstance()

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    private val _phoneError = MutableLiveData<String?>()
    val phoneError: LiveData<String?> = _phoneError

    fun login(phone: String) {
        if (!validatePhone(phone)) {
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.login(phone)
            result.fold(
                onSuccess = { user ->
                    _authState.value = AuthState.Success(user)
                },
                onFailure = { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Ошибка авторизации")
                }
            )
        }
    }

    private fun validatePhone(phone: String): Boolean {
        val cleanPhone = phone.replace("[^0-9+]".toRegex(), "")

        return when {
            cleanPhone.isEmpty() -> {
                _phoneError.value = "Введите номер телефона"
                false
            }
            cleanPhone.length < 11 -> {
                _phoneError.value = "Номер телефона слишком короткий"
                false
            }
            else -> {
                _phoneError.value = null
                true
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
        _phoneError.value = null
    }
}
