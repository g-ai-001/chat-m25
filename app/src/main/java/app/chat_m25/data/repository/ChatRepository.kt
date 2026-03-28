package app.chat_m25.data.repository

import app.chat_m25.data.local.dao.ChatSessionDao
import app.chat_m25.data.local.dao.MessageDao
import app.chat_m25.data.local.entity.ChatSessionEntity
import app.chat_m25.data.local.entity.MessageEntity
import app.chat_m25.domain.model.ChatSession
import app.chat_m25.domain.model.Message
import app.chat_m25.domain.model.MessageStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatSessionDao: ChatSessionDao,
    private val messageDao: MessageDao
) {
    fun getAllSessions(): Flow<List<ChatSession>> {
        return chatSessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getSessionById(id: Long): ChatSession? {
        return chatSessionDao.getSessionById(id)?.toDomain()
    }

    suspend fun createSession(name: String, avatar: String? = null): Long {
        val session = ChatSessionEntity(name = name, avatar = avatar)
        return chatSessionDao.insertSession(session)
    }

    suspend fun deleteSession(session: ChatSession) {
        chatSessionDao.deleteSession(session.toEntity())
    }

    suspend fun markAsRead(chatId: Long) {
        chatSessionDao.markAsRead(chatId)
    }

    fun getMessages(chatId: Long): Flow<List<Message>> {
        return messageDao.getMessagesByChatId(chatId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun sendMessage(chatId: Long, content: String): Long {
        val message = MessageEntity(
            chatId = chatId,
            content = content,
            isFromMe = true,
            timestamp = System.currentTimeMillis()
        )
        val messageId = messageDao.insertMessage(message)
        chatSessionDao.updateLastMessage(chatId, content, System.currentTimeMillis())
        return messageId
    }

    fun searchMessages(keyword: String): Flow<List<Message>> {
        return messageDao.searchMessages(keyword).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun updatePinned(chatId: Long, isPinned: Boolean) {
        chatSessionDao.updatePinned(chatId, isPinned)
    }

    suspend fun updateDoNotDisturb(chatId: Long, doNotDisturb: Boolean) {
        chatSessionDao.updateDoNotDisturb(chatId, doNotDisturb)
    }

    suspend fun updateBackgroundColor(chatId: Long, color: Long) {
        chatSessionDao.updateBackgroundColor(chatId, color)
    }

    private fun ChatSessionEntity.toDomain() = ChatSession(
        id = id,
        name = name,
        avatar = avatar,
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime,
        unreadCount = unreadCount,
        isPinned = isPinned,
        doNotDisturb = doNotDisturb,
        backgroundColor = backgroundColor
    )

    private fun ChatSession.toEntity() = ChatSessionEntity(
        id = id,
        name = name,
        avatar = avatar,
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime,
        unreadCount = unreadCount,
        isPinned = isPinned,
        doNotDisturb = doNotDisturb,
        backgroundColor = backgroundColor
    )

    private fun MessageEntity.toDomain() = Message(
        id = id,
        chatId = chatId,
        content = content,
        isFromMe = isFromMe,
        timestamp = timestamp,
        status = MessageStatus.valueOf(status)
    )
}
