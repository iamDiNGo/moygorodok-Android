package com.gorod.moygorodok.data.model

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class PinState {
    object Idle : PinState()
    object Loading : PinState()
    object Success : PinState()
    data class Error(val message: String, val attemptsLeft: Int) : PinState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val user: User) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
