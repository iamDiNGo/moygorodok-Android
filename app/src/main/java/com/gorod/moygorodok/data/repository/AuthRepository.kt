package com.gorod.moygorodok.data.repository

import com.gorod.moygorodok.data.model.MockUsers
import com.gorod.moygorodok.data.model.User
import kotlinx.coroutines.delay

class AuthRepository {

    private var currentUser: User? = null
    private var pinAttempts = 3

    suspend fun login(phone: String): Result<User> {
        // Имитация сетевой задержки
        delay(1000)

        val user = MockUsers.findByPhone(phone)
        return if (user != null) {
            currentUser = user
            Result.success(user)
        } else {
            Result.failure(Exception("Пользователь не найден"))
        }
    }

    suspend fun verifyPin(pin: String): Result<Boolean> {
        delay(500)

        val user = currentUser ?: return Result.failure(Exception("Пользователь не авторизован"))

        return if (user.pin == pin) {
            pinAttempts = 3
            Result.success(true)
        } else {
            pinAttempts--
            if (pinAttempts <= 0) {
                pinAttempts = 3
                Result.failure(Exception("Превышено количество попыток|0"))
            } else {
                Result.failure(Exception("Неверный PIN-код|$pinAttempts"))
            }
        }
    }

    suspend fun register(
        phone: String,
        firstName: String,
        lastName: String,
        email: String?,
        pin: String
    ): Result<User> {
        delay(1500)

        // Проверка, существует ли пользователь
        if (MockUsers.findByPhone(phone) != null) {
            return Result.failure(Exception("Пользователь с таким номером уже зарегистрирован"))
        }

        val newUser = User(
            id = (MockUsers.registeredUsers.size + 1).toString(),
            phone = phone,
            firstName = firstName,
            lastName = lastName,
            email = email,
            pin = pin
        )

        MockUsers.register(newUser)
        currentUser = newUser

        return Result.success(newUser)
    }

    suspend fun updateProfile(
        firstName: String,
        lastName: String,
        email: String?
    ): Result<User> {
        delay(500)

        val user = currentUser ?: return Result.failure(Exception("Пользователь не авторизован"))

        val updatedUser = user.copy(
            firstName = firstName,
            lastName = lastName,
            email = email
        )

        currentUser = updatedUser
        return Result.success(updatedUser)
    }

    suspend fun updateAvatar(avatarUrl: String): Result<User> {
        delay(500)

        val user = currentUser ?: return Result.failure(Exception("Пользователь не авторизован"))

        val updatedUser = user.copy(avatarUrl = avatarUrl)
        currentUser = updatedUser

        return Result.success(updatedUser)
    }

    fun getCurrentUser(): User? = currentUser

    fun isLoggedIn(): Boolean = currentUser != null

    fun logout() {
        currentUser = null
        pinAttempts = 3
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
        }
    }
}
