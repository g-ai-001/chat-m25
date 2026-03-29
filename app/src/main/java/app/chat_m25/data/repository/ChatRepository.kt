package app.chat_m25.data.repository

import app.chat_m25.data.local.dao.ChatSessionDao
import app.chat_m25.data.local.dao.MessageDao
import app.chat_m25.data.local.entity.ChatSessionEntity
import app.chat_m25.data.local.entity.MessageEntity
import app.chat_m25.data.mapper.EntityMapper.toDomain
import app.chat_m25.data.mapper.EntityMapper.toEntity
import app.chat_m25.domain.model.ChatSession
import app.chat_m25.domain.model.Message
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

    suspend fun deleteMessage(messageId: Long) {
        messageDao.deleteMessageById(messageId)
    }

    suspend fun toggleFavorite(messageId: Long, isFavorite: Boolean) {
        messageDao.updateFavorite(messageId, isFavorite)
    }

    fun getFavoriteMessages(): Flow<List<Message>> {
        return messageDao.getFavoriteMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun forwardMessage(messageId: Long, targetChatId: Long) {
        val originalMessage = messageDao.getMessagesByChatId(targetChatId)
        val message = MessageEntity(
            chatId = targetChatId,
            content = "转发消息",
            isFromMe = true,
            timestamp = System.currentTimeMillis(),
            forwardedFromId = messageId
        )
        messageDao.insertMessage(message)
        chatSessionDao.updateLastMessage(targetChatId, "转发消息", System.currentTimeMillis())
    }

    suspend fun replyMessage(chatId: Long, content: String, replyToId: Long): Long {
        val message = MessageEntity(
            chatId = chatId,
            content = content,
            isFromMe = true,
            timestamp = System.currentTimeMillis(),
            replyToId = replyToId
        )
        val messageId = messageDao.insertMessage(message)
        chatSessionDao.updateLastMessage(chatId, content, System.currentTimeMillis())
        return messageId
    }

    suspend fun createGroup(name: String, memberIds: List<Long>): Long {
        val session = ChatSessionEntity(name = name, avatar = null, isGroup = true)
        return chatSessionDao.insertSession(session)
    }

    fun getAllSessionsWithMembers(): Flow<List<ChatSession>> {
        return chatSessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getMediaMessages(chatId: Long): Flow<List<Message>> {
        return messageDao.getMediaMessages(chatId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
