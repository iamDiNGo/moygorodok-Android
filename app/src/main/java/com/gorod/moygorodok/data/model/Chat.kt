package com.gorod.moygorodok.data.model

data class ChatMessage(
    val id: String,
    val text: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val timestamp: String,
    val isRead: Boolean = true,
    val isOwn: Boolean = false
)

data class ChatUser(
    val id: String,
    val name: String,
    val avatar: String,
    val isOnline: Boolean = false,
    val lastSeen: String? = null
)

object MockChat {

    private val currentUserId = "current_user"

    fun getMessages(): List<ChatMessage> {
        return listOf(
            ChatMessage(
                id = "1",
                text = "Всем привет! Кто-нибудь знает, когда откроют новый парк?",
                senderId = "user1",
                senderName = "Анна",
                senderAvatar = "#E91E63",
                timestamp = "10:30",
                isOwn = false
            ),
            ChatMessage(
                id = "2",
                text = "Привет! Слышала, что на следующей неделе планируют открытие",
                senderId = "user2",
                senderName = "Мария",
                senderAvatar = "#9C27B0",
                timestamp = "10:32",
                isOwn = false
            ),
            ChatMessage(
                id = "3",
                text = "Отлично! А где он находится?",
                senderId = currentUserId,
                senderName = "Вы",
                senderAvatar = "#2196F3",
                timestamp = "10:33",
                isOwn = true
            ),
            ChatMessage(
                id = "4",
                text = "На улице Гагарина, рядом с торговым центром. Там большая зеленая зона будет 🌳",
                senderId = "user1",
                senderName = "Анна",
                senderAvatar = "#E91E63",
                timestamp = "10:35",
                isOwn = false
            ),
            ChatMessage(
                id = "5",
                text = "Спасибо за информацию! 👍",
                senderId = currentUserId,
                senderName = "Вы",
                senderAvatar = "#2196F3",
                timestamp = "10:36",
                isOwn = true
            ),
            ChatMessage(
                id = "6",
                text = "Кстати, там еще детскую площадку сделают и кафе",
                senderId = "user3",
                senderName = "Игорь",
                senderAvatar = "#4CAF50",
                timestamp = "10:40",
                isOwn = false
            ),
            ChatMessage(
                id = "7",
                text = "А парковка там будет?",
                senderId = "user4",
                senderName = "Дмитрий",
                senderAvatar = "#FF9800",
                timestamp = "10:42",
                isOwn = false
            ),
            ChatMessage(
                id = "8",
                text = "Да, подземная на 200 мест",
                senderId = "user1",
                senderName = "Анна",
                senderAvatar = "#E91E63",
                timestamp = "10:43",
                isOwn = false
            ),
            ChatMessage(
                id = "9",
                text = "Круто! Наконец-то нормальное место для прогулок появится",
                senderId = "user5",
                senderName = "Елена",
                senderAvatar = "#00BCD4",
                timestamp = "10:45",
                isOwn = false
            ),
            ChatMessage(
                id = "10",
                text = "Согласен, давно ждали! 🎉",
                senderId = currentUserId,
                senderName = "Вы",
                senderAvatar = "#2196F3",
                timestamp = "10:46",
                isOwn = true
            )
        )
    }

    fun getOnlineCount(): Int = 127

    fun getMembersCount(): Int = 1543
}
