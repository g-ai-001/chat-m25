package app.chat_m25.domain.model

data class Comment(
    val id: Long = 0,
    val momentId: Long,
    val userId: Long,
    val userName: String,
    val userAvatar: String = "",
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val replyToId: Long? = null,
    val replyToUserName: String? = null
)
