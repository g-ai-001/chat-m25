package app.chat_m25.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.chat_m25.data.local.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE momentId = :momentId ORDER BY timestamp ASC")
    fun getCommentsByMomentId(momentId: Long): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE id = :id")
    suspend fun getCommentById(id: Long): CommentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Delete
    suspend fun deleteComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteCommentById(id: Long)

    @Query("DELETE FROM comments WHERE momentId = :momentId")
    suspend fun deleteCommentsByMomentId(momentId: Long)

    @Query("SELECT COUNT(*) FROM comments WHERE momentId = :momentId")
    suspend fun getCommentCountByMomentId(momentId: Long): Int
}
