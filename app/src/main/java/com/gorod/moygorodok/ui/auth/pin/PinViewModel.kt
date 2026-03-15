package com.gorod.moygorodok.ui.auth.pin

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gorod.moygorodok.data.model.VerifyCodeState
import com.gorod.moygorodok.data.repository.*
import kotlinx.coroutines.launch

class PinViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository.getInstance(application)

    private val _verifyState = MutableLiveData<VerifyCodeState>(VerifyCodeState.Idle)
    val verifyState: LiveData<VerifyCodeState> = _verifyState

    private val _pinDigits = MutableLiveData<String>("")
    val pinDigits: LiveData<String> = _pinDigits

    private val _timerSeconds = MutableLiveData<Int>(0)
    val timerSeconds: LiveData<Int> = _timerSeconds

    private val _canResend = MutableLiveData<Boolean>(false)
    val canResend: LiveData<Boolean> = _canResend

    private var countDownTimer: CountDownTimer? = null

    var phone: String = ""
        private set
    var userExists: Boolean = true
        private set
    var code: String = ""
        private set

    fun init(phone: String, userExists: Boolean, retryAfter: Int) {
        this.phone = phone
        this.userExists = userExists
        startTimer(retryAfter)
    }

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

    fun verifyCode() {
        val pin = _pinDigits.value ?: ""
        if (pin.length != 4) return

        code = pin

        if (userExists) {
            verifyExistingUser(pin)
        } else {
            registerNewUser(pin)
        }
    }

    private fun verifyExistingUser(pin: String) {
        _verifyState.value = VerifyCodeState.Loading

        viewModelScope.launch {
            try {
                val result = repository.verifyCode(phone, pin)
                result.fold(
                    onSuccess = { authData ->
                        _verifyState.value = VerifyCodeState.Success(authData.user, authData.token)
                    },
                    onFailure = { exception ->
                        _verifyState.value = VerifyCodeState.Error(
                            exception.message ?: "Ошибка верификации"
                        )
                        clearPin()
                    }
                )
            } catch (e: AccountBlockedException) {
                _verifyState.value = VerifyCodeState.Blocked(e.reason)
                clearPin()
            } catch (e: InvalidCodeException) {
                _verifyState.value = VerifyCodeState.Error(e.message ?: "Неверный код")
                clearPin()
            } catch (e: UserNotFoundException) {
                _verifyState.value = VerifyCodeState.NeedRegistration(e.phone)
            }
        }
    }

    private fun registerNewUser(pin: String) {
        _verifyState.value = VerifyCodeState.NeedRegistration(phone)
    }

    fun resendCode() {
        _verifyState.value = VerifyCodeState.Loading

        viewModelScope.launch {
            try {
                val result = repository.sendCode(phone)
                result.fold(
                    onSuccess = { data ->
                        userExists = data.userExists
                        startTimer(data.retryAfter)
                        _verifyState.value = VerifyCodeState.Idle
                    },
                    onFailure = { exception ->
                        _verifyState.value = VerifyCodeState.Error(
                            exception.message ?: "Ошибка отправки кода"
                        )
                    }
                )
            } catch (e: CodeCooldownException) {
                e.retryAfter?.let { startTimer(it) }
                _verifyState.value = VerifyCodeState.Error(
                    e.message ?: "Код уже отправлен"
                )
            }
        }
    }

    private fun startTimer(seconds: Int) {
        countDownTimer?.cancel()
        _canResend.value = false
        _timerSeconds.value = seconds

        countDownTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _timerSeconds.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                _timerSeconds.value = 0
                _canResend.value = true
            }
        }.start()
    }

    fun resetState() {
        _verifyState.value = VerifyCodeState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
