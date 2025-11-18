package com.gorod.moygorodok.ui.auth.pin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.PinState
import com.gorod.moygorodok.data.repository.AuthRepository
import kotlinx.coroutines.launch

class PinViewModel : ViewModel() {

    private val repository = AuthRepository.getInstance()

    private val _pinState = MutableLiveData<PinState>(PinState.Idle)
    val pinState: LiveData<PinState> = _pinState

    private val _pinDigits = MutableLiveData<String>("")
    val pinDigits: LiveData<String> = _pinDigits

    fun addDigit(digit: String) {
        val currentPin = _pinDigits.value ?: ""
        if (currentPin.length < 4) {
            _pinDigits.value = currentPin + digit
        }
    }

    fun removeDigit() {
        val currentPin = _pinDigits.value ?: ""
        if (currentPin.isNotEmpty()) {
            _pinDigits.value = currentPin.dropLast(1)
        }
    }

    fun clearPin() {
        _pinDigits.value = ""
    }

    fun verifyPin() {
        val pin = _pinDigits.value ?: ""
        if (pin.length != 4) {
            return
        }

        _pinState.value = PinState.Loading

        viewModelScope.launch {
            val result = repository.verifyPin(pin)
            result.fold(
                onSuccess = {
                    _pinState.value = PinState.Success
                },
                onFailure = { exception ->
                    val message = exception.message ?: "Ошибка"
                    val parts = message.split("|")
                    val errorMessage = parts[0]
                    val attempts = parts.getOrNull(1)?.toIntOrNull() ?: 0

                    _pinState.value = PinState.Error(errorMessage, attempts)
                    clearPin()
                }
            )
        }
    }

    fun resetState() {
        _pinState.value = PinState.Idle
    }
}
