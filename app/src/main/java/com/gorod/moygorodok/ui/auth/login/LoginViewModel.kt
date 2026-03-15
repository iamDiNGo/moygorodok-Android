package com.gorod.moygorodok.ui.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.SendCodeState
import com.gorod.moygorodok.data.repository.AuthRepository
import com.gorod.moygorodok.data.repository.CodeCooldownException
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository.getInstance(application)

    private val _sendCodeState = MutableLiveData<SendCodeState>(SendCodeState.Idle)
    val sendCodeState: LiveData<SendCodeState> = _sendCodeState

    private val _phoneError = MutableLiveData<String?>()
    val phoneError: LiveData<String?> = _phoneError

    private var _phone: String = ""
    val phone: String get() = _phone

    fun sendCode(phone: String) {
        val cleanPhone = formatPhone(phone)
        if (!validatePhone(cleanPhone)) {
            return
        }

        _phone = cleanPhone
        _sendCodeState.value = SendCodeState.Loading

        viewModelScope.launch {
            try {
                val result = repository.sendCode(cleanPhone)
                result.fold(
                    onSuccess = { data ->
                        _sendCodeState.value = SendCodeState.Success(
                            userExists = data.userExists,
                            retryAfter = data.retryAfter
                        )
                    },
                    onFailure = { exception ->
                        _sendCodeState.value = SendCodeState.Error(
                            exception.message ?: "Ошибка отправки кода"
                        )
                    }
                )
            } catch (e: CodeCooldownException) {
                _sendCodeState.value = SendCodeState.Error(
                    e.message ?: "Код уже отправлен",
                    e.retryAfter
                )
            }
        }
    }

    private fun formatPhone(phone: String): String {
        val digits = phone.replace("[^0-9]".toRegex(), "")
        return if (digits.startsWith("7") || digits.startsWith("8")) {
            "+7${digits.substring(1)}"
        } else if (digits.startsWith("+")) {
            phone.replace("[^0-9+]".toRegex(), "")
        } else {
            "+7$digits"
        }
    }

    private fun validatePhone(phone: String): Boolean {
        val regex = Regex("^[+]?[0-9\\s\\-()]{7,15}$")
        return when {
            phone.isBlank() -> {
                _phoneError.value = "Введите номер телефона"
                false
            }
            !regex.matches(phone) -> {
                _phoneError.value = "Неверный формат номера"
                false
            }
            else -> {
                _phoneError.value = null
                true
            }
        }
    }

    fun resetState() {
        _sendCodeState.value = SendCodeState.Idle
        _phoneError.value = null
    }
}
