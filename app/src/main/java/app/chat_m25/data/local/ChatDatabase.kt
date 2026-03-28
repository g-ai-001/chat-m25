package app.chat_m25.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import app.chat_m25.data.local.dao.ChatSessionDao
import app.chat_m25.data.local.dao.ContactDao
import app.chat_m25.data.local.dao.MessageDao
import app.chat_m25.data.local.entity.ChatSessionEntity
import app.chat_m25.data.local.entity.ContactEntity
import app.chat_m25.data.local.entity.MessageEntity

@Database(
    entities = [
        ChatSessionEntity::class,
        MessageEntity::class,
        ContactEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatSessionDao(): ChatSessionDao
    abstract fun messageDao(): MessageDao
    abstract fun contactDao(): ContactDao
}
