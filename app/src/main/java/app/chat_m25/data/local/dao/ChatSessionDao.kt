package app.chat_m25.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.chat_m25.data.local.entity.ChatSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {
    @Query("SELECT * FROM chat_sessions ORDER BY lastMessageTime DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): ChatSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity): Long

    @Update
    suspend fun updateSession(session: ChatSessionEntity)

    @Delete
    suspend fun deleteSession(session: ChatSessionEntity)

    @Query("UPDATE chat_sessions SET unreadCount = 0 WHERE id = :chatId")
    suspend fun markAsRead(chatId: Long)

    @Query("UPDATE chat_sessions SET lastMessage = :message, lastMessageTime = :time WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: Long, message: String, time: Long)
}
