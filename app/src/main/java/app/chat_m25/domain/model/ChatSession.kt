package app.chat_m25.domain.model

data class ChatSession(
    val id: Long = 0,
    val name: String,
    val avatar: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val isPinned: Boolean = false,
    val doNotDisturb: Boolean = false,
    val backgroundColor: Long = 0xFFFFFFFF
)
