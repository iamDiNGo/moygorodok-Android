package com.gorod.moygorodok.data.remote.model

import com.gorod.moygorodok.data.model.User
import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null
)

data class ValidationErrorResponse(
    val message: String? = null,
    val errors: Map<String, List<String>>? = null
)

data class SendCodeData(
    val phone: String,
    @SerializedName("user_exists")
    val userExists: Boolean,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("retry_after")
    val retryAfter: Int
)

data class AuthData(
    val user: User,
    val token: String,
    @SerializedName("token_type")
    val tokenType: String
)

data class BlockedData(
    @SerializedName("blocked_at")
    val blockedAt: String?,
    @SerializedName("blocked_reason")
    val blockedReason: String?
)

data class SendCodeRequest(
    val phone: String
)

data class VerifyCodeRequest(
    val phone: String,
    val code: String
)
