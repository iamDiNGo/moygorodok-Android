package com.gorod.moygorodok.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.RegisterState
import com.gorod.moygorodok.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository.getInstance()

    private val _registerState = MutableLiveData<RegisterState>(RegisterState.Idle)
    val registerState: LiveData<RegisterState> = _registerState

    private val _phoneError = MutableLiveData<String?>()
    val phoneError: LiveData<String?> = _phoneError

    private val _firstNameError = MutableLiveData<String?>()
    val firstNameError: LiveData<String?> = _firstNameError

    private val _lastNameError = MutableLiveData<String?>()
    val lastNameError: LiveData<String?> = _lastNameError

    private val _pinError = MutableLiveData<String?>()
    val pinError: LiveData<String?> = _pinError

    private val _pinConfirmError = MutableLiveData<String?>()
    val pinConfirmError: LiveData<String?> = _pinConfirmError

    fun register(
        phone: String,
        firstName: String,
        lastName: String,
        email: String?,
        pin: String,
        pinConfirm: String
    ) {
        if (!validateInput(phone, firstName, lastName, pin, pinConfirm)) {
            return
        }

        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            val result = repository.register(phone, firstName, lastName, email, pin)
            result.fold(
                onSuccess = { user ->
                    _registerState.value = RegisterState.Success(user)
                },
                onFailure = { exception ->
                    _registerState.value = RegisterState.Error(
                        exception.message ?: "Ошибка регистрации"
                    )
                }
            )
        }
    }

    private fun validateInput(
        phone: String,
        firstName: String,
        lastName: String,
        pin: String,
        pinConfirm: String
    ): Boolean {
        var isValid = true

        val cleanPhone = phone.replace("[^0-9+]".toRegex(), "")
        if (cleanPhone.isEmpty()) {
            _phoneError.value = "Введите номер телефона"
            isValid = false
        } else if (cleanPhone.length < 11) {
            _phoneError.value = "Номер телефона слишком короткий"
            isValid = false
        } else {
            _phoneError.value = null
        }

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

        if (pin.length != 4) {
            _pinError.value = "PIN должен содержать 4 цифры"
            isValid = false
        } else if (!pin.all { it.isDigit() }) {
            _pinError.value = "PIN должен содержать только цифры"
            isValid = false
        } else {
            _pinError.value = null
        }

        if (pin != pinConfirm) {
            _pinConfirmError.value = "PIN-коды не совпадают"
            isValid = false
        } else {
            _pinConfirmError.value = null
        }

        return isValid
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }

    fun clearErrors() {
        _phoneError.value = null
        _firstNameError.value = null
        _lastNameError.value = null
        _pinError.value = null
        _pinConfirmError.value = null
    }
}
