package app.chat_m25.data.mapper

import app.chat_m25.data.local.entity.ChatSessionEntity
import app.chat_m25.data.local.entity.ContactEntity
import app.chat_m25.data.local.entity.MessageEntity
import app.chat_m25.data.local.entity.MomentEntity
import app.chat_m25.domain.model.ChatSession
import app.chat_m25.domain.model.Contact
import app.chat_m25.domain.model.Message
import app.chat_m25.domain.model.MessageStatus
import app.chat_m25.domain.model.Moment
import org.json.JSONArray

object EntityMapper {

    fun ChatSessionEntity.toDomain() = ChatSession(
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

    fun ChatSession.toEntity() = ChatSessionEntity(
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

    fun MessageEntity.toDomain() = Message(
        id = id,
        chatId = chatId,
        content = content,
        isFromMe = isFromMe,
        timestamp = timestamp,
        status = MessageStatus.valueOf(status),
        isFavorite = isFavorite
    )

    fun Message.toEntity() = MessageEntity(
        id = id,
        chatId = chatId,
        content = content,
        isFromMe = isFromMe,
        timestamp = timestamp,
        status = status.name,
        isFavorite = isFavorite
    )

    fun ContactEntity.toDomain() = Contact(
        id = id,
        name = name,
        avatar = avatar,
        remark = remark,
        phone = phone,
        isStarred = isStarred
    )

    fun Contact.toEntity() = ContactEntity(
        id = id,
        name = name,
        avatar = avatar,
        remark = remark,
        phone = phone,
        isStarred = isStarred
    )

    fun MomentEntity.toDomain(): Moment {
        val imagesList = try {
            val jsonArray = JSONArray(images)
            (0 until jsonArray.length()).map { jsonArray.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }
        return Moment(
            id = id,
            userId = userId,
            userName = userName,
            userAvatar = userAvatar,
            content = content,
            images = imagesList,
            timestamp = timestamp,
            likeCount = likeCount,
            commentCount = commentCount,
            isLiked = isLiked
        )
    }

    fun Moment.toEntity(): MomentEntity {
        val imagesJson = JSONArray(images).toString()
        return MomentEntity(
            id = id,
            userId = userId,
            userName = userName,
            userAvatar = userAvatar,
            content = content,
            images = imagesJson,
            timestamp = timestamp,
            likeCount = likeCount,
            commentCount = commentCount,
            isLiked = isLiked
        )
    }
}
