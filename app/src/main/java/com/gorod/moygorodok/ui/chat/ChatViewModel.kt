package com.gorod.moygorodok.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gorod.moygorodok.data.model.ChatMessage
import com.gorod.moygorodok.data.model.MockChat

class ChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _onlineCount = MutableLiveData<Int>()
    val onlineCount: LiveData<Int> = _onlineCount

    private val _membersCount = MutableLiveData<Int>()
    val membersCount: LiveData<Int> = _membersCount

    init {
        loadMessages()
    }

    private fun loadMessages() {
        _messages.value = MockChat.getMessages()
        _onlineCount.value = MockChat.getOnlineCount()
        _membersCount.value = MockChat.getMembersCount()
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            text = text,
            senderId = "current_user",
            senderName = "Вы",
            senderAvatar = "#2196F3",
            timestamp = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date()),
            isOwn = true
        )
        currentMessages.add(newMessage)
        _messages.value = currentMessages
    }

    fun refresh() {
        loadMessages()
    }
}
