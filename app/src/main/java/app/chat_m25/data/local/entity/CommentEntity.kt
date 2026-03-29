package app.chat_m25.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(
            entity = MomentEntity::class,
            parentColumns = ["id"],
            childColumns = ["momentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["momentId"])]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
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
