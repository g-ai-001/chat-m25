package app.chat_m25.domain.model

data class Moment(
    val id: Long = 0,
    val userId: Long,
    val userName: String,
    val userAvatar: String = "",
    val content: String,
    val images: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false
)