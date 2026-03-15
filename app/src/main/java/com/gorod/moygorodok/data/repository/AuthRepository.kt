package com.gorod.moygorodok.data.repository

import android.content.Context
import com.gorod.moygorodok.data.local.TokenManager
import com.gorod.moygorodok.data.model.User
import com.gorod.moygorodok.data.remote.ApiClient
import com.gorod.moygorodok.data.remote.model.*
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AuthRepository(context: Context) {

    private val api = ApiClient.apiService
    private val tokenManager = TokenManager.getInstance(context)
    private val gson = Gson()

    suspend fun sendCode(phone: String): Result<SendCodeData> {
        return try {
            val response = api.sendCode(SendCodeRequest(phone))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Ошибка отправки кода"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                when (response.code()) {
                    429 -> {
                        val apiError = tryParseError(errorBody)
                        val retryAfter = tryParseRetryAfter(errorBody)
                        val message = apiError?.message ?: "Слишком много попыток. Попробуйте позже"
                        Result.failure(CodeCooldownException(message, retryAfter))
                    }
                    422 -> {
                        val validationError = tryParseValidationError(errorBody)
                        val message = validationError?.errors?.values?.flatten()?.firstOrNull()
                            ?: validationError?.message ?: "Ошибка валидации"
                        Result.failure(Exception(message))
                    }
                    else -> {
                        val apiError = tryParseError(errorBody)
                        Result.failure(Exception(apiError?.message ?: "Ошибка сервера"))
                    }
                }
            }
        } catch (e: Exception) {
            if (e is CodeCooldownException) throw e
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun verifyCode(phone: String, code: String): Result<AuthData> {
        return try {
            val response = api.verifyCode(VerifyCodeRequest(phone, code))
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    saveAuth(body.data)
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Ошибка верификации"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                when (response.code()) {
                    404 -> {
                        Result.failure(UserNotFoundException(phone))
                    }
                    422 -> {
                        val apiError = tryParseError(errorBody)
                        Result.failure(InvalidCodeException(apiError?.message ?: "Неверный или просроченный код"))
                    }
                    403 -> {
                        val apiError = tryParseError(errorBody)
                        val blockedData = tryParseBlockedData(errorBody)
                        Result.failure(AccountBlockedException(
                            blockedData?.blockedReason ?: apiError?.message ?: "Аккаунт заблокирован"
                        ))
                    }
                    else -> {
                        val apiError = tryParseError(errorBody)
                        Result.failure(Exception(apiError?.message ?: "Ошибка сервера"))
                    }
                }
            }
        } catch (e: Exception) {
            if (e is UserNotFoundException || e is InvalidCodeException || e is AccountBlockedException) throw e
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun register(
        name: String,
        phone: String,
        code: String,
        gender: String,
        avatarFile: File? = null
    ): Result<AuthData> {
        return try {
            val textType = "text/plain".toMediaTypeOrNull()
            val namePart = name.toRequestBody(textType)
            val phonePart = phone.toRequestBody(textType)
            val codePart = code.toRequestBody(textType)
            val genderPart = gender.toRequestBody(textType)

            val avatarPart = avatarFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("avatar", it.name, requestFile)
            }

            val response = api.register(namePart, phonePart, codePart, genderPart, avatarPart)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    saveAuth(body.data)
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Ошибка регистрации"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                when (response.code()) {
                    422 -> {
                        val apiError = tryParseError(errorBody)
                        if (apiError?.error == "invalid_code") {
                            Result.failure(InvalidCodeException(apiError.message ?: "Неверный или просроченный код"))
                        } else {
                            val validationError = tryParseValidationError(errorBody)
                            val message = validationError?.errors?.values?.flatten()?.firstOrNull()
                                ?: validationError?.message ?: "Ошибка валидации"
                            Result.failure(Exception(message))
                        }
                    }
                    else -> {
                        val apiError = tryParseError(errorBody)
                        Result.failure(Exception(apiError?.message ?: "Ошибка сервера"))
                    }
                }
            }
        } catch (e: Exception) {
            if (e is InvalidCodeException) throw e
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun getProfile(): Result<User> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    tokenManager.user = body.data
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Ошибка загрузки профиля"))
                }
            } else {
                if (response.code() == 401) {
                    tokenManager.clear()
                    Result.failure(UnauthorizedException())
                } else {
                    Result.failure(Exception("Ошибка сервера"))
                }
            }
        } catch (e: Exception) {
            if (e is UnauthorizedException) throw e
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun updateProfile(
        name: String? = null,
        email: String? = null,
        gender: String? = null,
        avatarFile: File? = null
    ): Result<User> {
        return try {
            val textType = "text/plain".toMediaTypeOrNull()
            val methodPart = "PUT".toRequestBody(textType)
            val namePart = name?.toRequestBody(textType)
            val emailPart = email?.toRequestBody(textType)
            val genderPart = gender?.toRequestBody(textType)

            val avatarPart = avatarFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("avatar", it.name, requestFile)
            }

            val response = api.updateProfile(methodPart, namePart, emailPart, genderPart, avatarPart)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    tokenManager.user = body.data
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Ошибка обновления профиля"))
                }
            } else {
                if (response.code() == 401) {
                    tokenManager.clear()
                    Result.failure(UnauthorizedException())
                } else {
                    val errorBody = response.errorBody()?.string()
                    val validationError = tryParseValidationError(errorBody)
                    val message = validationError?.errors?.values?.flatten()?.firstOrNull()
                        ?: validationError?.message ?: "Ошибка обновления"
                    Result.failure(Exception(message))
                }
            }
        } catch (e: Exception) {
            if (e is UnauthorizedException) throw e
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun deleteAvatar(): Result<User> {
        return try {
            val response = api.deleteAvatar()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    tokenManager.user = body.data
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Ошибка удаления аватара"))
                }
            } else {
                Result.failure(Exception("Ошибка сервера"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            tokenManager.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            tokenManager.clear()
            Result.success(Unit)
        }
    }

    suspend fun logoutAll(): Result<Unit> {
        return try {
            api.logoutAll()
            tokenManager.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            tokenManager.clear()
            Result.success(Unit)
        }
    }

    fun getCurrentUser(): User? = tokenManager.user

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun getToken(): String? = tokenManager.token

    private fun saveAuth(authData: AuthData) {
        tokenManager.token = authData.token
        tokenManager.user = authData.user
    }

    private fun tryParseError(errorBody: String?): ApiResponse<Any>? {
        return try {
            errorBody?.let { gson.fromJson(it, ApiResponse::class.java) as? ApiResponse<Any> }
        } catch (e: Exception) {
            null
        }
    }

    private fun tryParseValidationError(errorBody: String?): ValidationErrorResponse? {
        return try {
            errorBody?.let { gson.fromJson(it, ValidationErrorResponse::class.java) }
        } catch (e: Exception) {
            null
        }
    }

    private fun tryParseRetryAfter(errorBody: String?): Int? {
        return try {
            errorBody?.let {
                val map = gson.fromJson(it, Map::class.java)
                val data = map["data"] as? Map<*, *>
                (data?.get("retry_after") as? Double)?.toInt()
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun tryParseBlockedData(errorBody: String?): BlockedData? {
        return try {
            errorBody?.let {
                val map = gson.fromJson(it, Map::class.java)
                val data = map["data"] as? Map<*, *>
                data?.let { d ->
                    BlockedData(
                        blockedAt = d["blocked_at"] as? String,
                        blockedReason = d["blocked_reason"] as? String
                    )
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(context: Context): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}

class CodeCooldownException(message: String, val retryAfter: Int?) : Exception(message)
class UserNotFoundException(val phone: String) : Exception("Пользователь не найден")
class InvalidCodeException(message: String) : Exception(message)
class AccountBlockedException(val reason: String) : Exception(reason)
class UnauthorizedException : Exception("Сессия истекла")
