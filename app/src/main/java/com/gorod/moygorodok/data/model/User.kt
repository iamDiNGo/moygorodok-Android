package com.gorod.moygorodok.data.model

data class User(
    val id: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val avatarUrl: String? = null,
    val pin: String? = null
)

// Фиктивные данные для тестирования
object MockUsers {
    val testUser = User(
        id = "1",
        phone = "+7 999 123 45 67",
        firstName = "Иван",
        lastName = "Петров",
        email = "ivan@example.com",
        avatarUrl = null,
        pin = "1234"
    )

    val registeredUsers = mutableListOf(testUser)

    fun findByPhone(phone: String): User? {
        return registeredUsers.find { it.phone.replace(" ", "") == phone.replace(" ", "") }
    }

    fun register(user: User) {
        registeredUsers.add(user)
    }
}
