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
    @Query("SELECT * FROM chat_sessions ORDER BY isPinned DESC, lastMessageTime DESC")
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

    @Query("UPDATE chat_sessions SET isPinned = :isPinned WHERE id = :chatId")
    suspend fun updatePinned(chatId: Long, isPinned: Boolean)

    @Query("UPDATE chat_sessions SET doNotDisturb = :doNotDisturb WHERE id = :chatId")
    suspend fun updateDoNotDisturb(chatId: Long, doNotDisturb: Boolean)

    @Query("UPDATE chat_sessions SET backgroundColor = :color WHERE id = :chatId")
    suspend fun updateBackgroundColor(chatId: Long, color: Long)

    @Query("UPDATE chat_sessions SET groupAvatar = :avatar WHERE id = :chatId")
    suspend fun updateGroupAvatar(chatId: Long, avatar: String?)

    @Query("UPDATE chat_sessions SET groupAnnouncement = :announcement WHERE id = :chatId")
    suspend fun updateGroupAnnouncement(chatId: Long, announcement: String)

    @Query("UPDATE chat_sessions SET name = :name WHERE id = :chatId")
    suspend fun updateGroupName(chatId: Long, name: String)
}
