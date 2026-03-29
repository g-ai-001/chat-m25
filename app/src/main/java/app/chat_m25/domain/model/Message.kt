package app.chat_m25.domain.model

data class Message(
    val id: Long = 0,
    val chatId: Long,
    val content: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT,
    val isFavorite: Boolean = false,
    val replyToId: Long? = null,
    val forwardedFromId: Long? = null,
    val mediaType: String? = null,
    val mediaPath: String? = null,
    val duration: Int = 0
)

enum class MessageStatus {
    SENDING, SENT, DELIVERED, READ, RECALLED
}

enum class MediaType {
    IMAGE, VIDEO, FILE, AUDIO
}
