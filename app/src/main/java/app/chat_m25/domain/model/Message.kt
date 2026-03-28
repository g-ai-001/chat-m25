package app.chat_m25.domain.model

data class Message(
    val id: Long = 0,
    val chatId: Long,
    val content: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    SENDING, SENT, READ
}
