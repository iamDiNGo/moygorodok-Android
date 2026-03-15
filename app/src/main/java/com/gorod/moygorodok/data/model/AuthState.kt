package com.gorod.moygorodok.data.model

sealed class SendCodeState {
    object Idle : SendCodeState()
    object Loading : SendCodeState()
    data class Success(val userExists: Boolean, val retryAfter: Int) : SendCodeState()
    data class Error(val message: String, val retryAfter: Int? = null) : SendCodeState()
}

sealed class VerifyCodeState {
    object Idle : VerifyCodeState()
    object Loading : VerifyCodeState()
    data class Success(val user: User, val token: String) : VerifyCodeState()
    data class NeedRegistration(val phone: String) : VerifyCodeState()
    data class Error(val message: String) : VerifyCodeState()
    data class Blocked(val reason: String?) : VerifyCodeState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val user: User, val token: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
    data class CodeExpired(val message: String) : RegisterState()
}

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}
