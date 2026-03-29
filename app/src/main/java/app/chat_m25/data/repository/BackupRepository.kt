package app.chat_m25.data.repository

import android.content.Context
import android.os.Environment
import app.chat_m25.data.local.dao.ChatSessionDao
import app.chat_m25.data.local.dao.ContactDao
import app.chat_m25.data.local.dao.MessageDao
import app.chat_m25.data.local.dao.MomentDao
import app.chat_m25.data.mapper.EntityMapper.toEntity
import app.chat_m25.data.mapper.EntityMapper.toDomain
import app.chat_m25.domain.model.ChatSession
import app.chat_m25.domain.model.Contact
import app.chat_m25.domain.model.Message
import app.chat_m25.domain.model.Moment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val chatSessionDao: ChatSessionDao,
    private val messageDao: MessageDao,
    private val contactDao: ContactDao,
    private val momentDao: MomentDao
) {
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    suspend fun exportData(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val exportData = JSONObject()

            // Export chat sessions
            val sessions = chatSessionDao.getAllSessions().first()
            val sessionsArray = JSONArray()
            sessions.forEach { session ->
                val sessionJson = JSONObject().apply {
                    put("id", session.id)
                    put("name", session.name)
                    put("avatar", session.avatar ?: "")
                    put("lastMessage", session.lastMessage)
                    put("lastMessageTime", session.lastMessageTime)
                    put("unreadCount", session.unreadCount)
                    put("isPinned", session.isPinned)
                    put("doNotDisturb", session.doNotDisturb)
                    put("backgroundColor", session.backgroundColor)
                    put("isGroup", session.isGroup)
                    put("groupAvatar", session.groupAvatar ?: "")
                    put("groupAnnouncement", session.groupAnnouncement)
                }
                sessionsArray.put(sessionJson)
            }
            exportData.put("chatSessions", sessionsArray)

            // Export contacts
            val contacts = contactDao.getAllContactsList()
            val contactsArray = JSONArray()
            contacts.forEach { contact ->
                val contactJson = JSONObject().apply {
                    put("id", contact.id)
                    put("name", contact.name)
                    put("avatar", contact.avatar ?: "")
                    put("remark", contact.remark)
                    put("phone", contact.phone)
                    put("isStarred", contact.isStarred)
                }
                contactsArray.put(contactJson)
            }
            exportData.put("contacts", contactsArray)

            // Export messages (only recent 1000 to keep file size manageable)
            val messages = messageDao.getAllMessages().takeLast(1000)
            val messagesArray = JSONArray()
            messages.forEach { message ->
                val messageJson = JSONObject().apply {
                    put("id", message.id)
                    put("chatId", message.chatId)
                    put("content", message.content)
                    put("isFromMe", message.isFromMe)
                    put("timestamp", message.timestamp)
                    put("status", message.status)
                    put("isFavorite", message.isFavorite)
                    put("replyToId", message.replyToId ?: 0)
                    put("forwardedFromId", message.forwardedFromId ?: 0)
                    put("mediaType", message.mediaType ?: "")
                    put("mediaPath", message.mediaPath ?: "")
                }
                messagesArray.put(messageJson)
            }
            exportData.put("messages", messagesArray)

            // Export moments
            val moments = momentDao.getAllMoments().first()
            val momentsArray = JSONArray()
            moments.forEach { moment ->
                val momentJson = JSONObject().apply {
                    put("id", moment.id)
                    put("userId", moment.userId)
                    put("userName", moment.userName)
                    put("userAvatar", moment.userAvatar ?: "")
                    put("content", moment.content)
                    put("images", moment.images)
                    put("timestamp", moment.timestamp)
                    put("likeCount", moment.likeCount)
                    put("commentCount", moment.commentCount)
                    put("isLiked", moment.isLiked)
                }
                momentsArray.put(momentJson)
            }
            exportData.put("moments", momentsArray)

            exportData.put("exportTime", System.currentTimeMillis())
            exportData.put("version", 1)

            // Save to file
            val fileName = "chat_m25_backup_${dateFormat.format(Date())}.json"
            val backupDir = File(context.getExternalFilesDir(null), "backups")
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
            val backupFile = File(backupDir, fileName)
            backupFile.writeText(exportData.toString())

            Result.success(backupFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importData(filePath: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext Result.failure(Exception("备份文件不存在"))
            }

            val jsonContent = file.readText()
            val importData = JSONObject(jsonContent)

            var importedCount = 0

            // Import chat sessions
            if (importData.has("chatSessions")) {
                val sessionsArray = importData.getJSONArray("chatSessions")
                for (i in 0 until sessionsArray.length()) {
                    val sessionJson = sessionsArray.getJSONObject(i)
                    val session = app.chat_m25.data.local.entity.ChatSessionEntity(
                        id = 0, // Let Room auto-generate
                        name = sessionJson.getString("name"),
                        avatar = sessionJson.getString("avatar").takeIf { it.isNotEmpty() },
                        lastMessage = sessionJson.getString("lastMessage"),
                        lastMessageTime = sessionJson.getLong("lastMessageTime"),
                        unreadCount = sessionJson.getInt("unreadCount"),
                        isPinned = sessionJson.getBoolean("isPinned"),
                        doNotDisturb = sessionJson.getBoolean("doNotDisturb"),
                        backgroundColor = sessionJson.getLong("backgroundColor"),
                        isGroup = sessionJson.getBoolean("isGroup"),
                        groupAvatar = sessionJson.getString("groupAvatar").takeIf { it.isNotEmpty() },
                        groupAnnouncement = sessionJson.getString("groupAnnouncement")
                    )
                    chatSessionDao.insertSession(session)
                    importedCount++
                }
            }

            // Import contacts
            if (importData.has("contacts")) {
                val contactsArray = importData.getJSONArray("contacts")
                for (i in 0 until contactsArray.length()) {
                    val contactJson = contactsArray.getJSONObject(i)
                    val contact = app.chat_m25.data.local.entity.ContactEntity(
                        id = 0,
                        name = contactJson.getString("name"),
                        avatar = contactJson.getString("avatar").takeIf { it.isNotEmpty() },
                        remark = contactJson.getString("remark"),
                        phone = contactJson.getString("phone"),
                        isStarred = contactJson.getBoolean("isStarred")
                    )
                    contactDao.insertContact(contact)
                    importedCount++
                }
            }

            // Import messages
            if (importData.has("messages")) {
                val messagesArray = importData.getJSONArray("messages")
                for (i in 0 until messagesArray.length()) {
                    val messageJson = messagesArray.getJSONObject(i)
                    val message = app.chat_m25.data.local.entity.MessageEntity(
                        id = 0,
                        chatId = messageJson.getLong("chatId"),
                        content = messageJson.getString("content"),
                        isFromMe = messageJson.getBoolean("isFromMe"),
                        timestamp = messageJson.getLong("timestamp"),
                        status = messageJson.getString("status"),
                        isFavorite = messageJson.getBoolean("isFavorite"),
                        replyToId = messageJson.getLong("replyToId").takeIf { it != 0L },
                        forwardedFromId = messageJson.getLong("forwardedFromId").takeIf { it != 0L },
                        mediaType = messageJson.getString("mediaType").takeIf { it.isNotEmpty() },
                        mediaPath = messageJson.getString("mediaPath").takeIf { it.isNotEmpty() }
                    )
                    messageDao.insertMessage(message)
                    importedCount++
                }
            }

            // Import moments
            if (importData.has("moments")) {
                val momentsArray = importData.getJSONArray("moments")
                for (i in 0 until momentsArray.length()) {
                    val momentJson = momentsArray.getJSONObject(i)
                    val moment = app.chat_m25.data.local.entity.MomentEntity(
                        id = 0,
                        userId = momentJson.getLong("userId"),
                        userName = momentJson.getString("userName"),
                        userAvatar = momentJson.optString("userAvatar", null) ?: "",
                        content = momentJson.getString("content"),
                        images = momentJson.getString("images"),
                        timestamp = momentJson.getLong("timestamp"),
                        likeCount = momentJson.getInt("likeCount"),
                        commentCount = momentJson.getInt("commentCount"),
                        isLiked = momentJson.getBoolean("isLiked")
                    )
                    momentDao.insertMoment(moment)
                    importedCount++
                }
            }

            Result.success(importedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getBackupFiles(): List<File> {
        val backupDir = File(context.getExternalFilesDir(null), "backups")
        return if (backupDir.exists()) {
            backupDir.listFiles()?.filter { it.extension == "json" }?.sortedByDescending { it.lastModified() } ?: emptyList()
        } else {
            emptyList()
        }
    }
}