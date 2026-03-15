package com.gorod.moygorodok.ui.auth.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.RegisterState
import com.gorod.moygorodok.data.repository.AuthRepository
import com.gorod.moygorodok.data.repository.InvalidCodeException
import kotlinx.coroutines.launch
import java.io.File

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository.getInstance(application)

    private val _registerState = MutableLiveData<RegisterState>(RegisterState.Idle)
    val registerState: LiveData<RegisterState> = _registerState

    private val _nameError = MutableLiveData<String?>()
    val nameError: LiveData<String?> = _nameError

    private val _genderError = MutableLiveData<String?>()
    val genderError: LiveData<String?> = _genderError

    var phone: String = ""
        private set
    var code: String = ""
        private set

    fun init(phone: String, code: String) {
        this.phone = phone
        this.code = code
    }

    fun register(name: String, gender: String?, avatarFile: File? = null) {
        if (!validateInput(name, gender)) {
            return
        }

        _registerState.value = RegisterState.Loading

        viewModelScope.launch {
            try {
                val result = repository.register(name, phone, code, gender!!, avatarFile)
                result.fold(
                    onSuccess = { authData ->
                        _registerState.value = RegisterState.Success(authData.user, authData.token)
                    },
                    onFailure = { exception ->
                        _registerState.value = RegisterState.Error(
                            exception.message ?: "Ошибка регистрации"
                        )
                    }
                )
            } catch (e: InvalidCodeException) {
                _registerState.value = RegisterState.CodeExpired(
                    e.message ?: "Неверный или просроченный код"
                )
            }
        }
    }

    private fun validateInput(name: String, gender: String?): Boolean {
        var isValid = true

        if (name.isBlank()) {
            _nameError.value = "Введите имя"
            isValid = false
        } else if (name.length > 255) {
            _nameError.value = "Имя слишком длинное"
            isValid = false
        } else {
            _nameError.value = null
        }

        if (gender.isNullOrBlank()) {
            _genderError.value = "Выберите пол"
            isValid = false
        } else {
            _genderError.value = null
        }

        return isValid
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}
