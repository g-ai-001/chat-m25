package app.chat_m25.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moments")
data class MomentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val userName: String,
    val userAvatar: String = "",
    val content: String,
    val images: String = "", // JSON encoded list
    val timestamp: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLiked: Boolean = false
)