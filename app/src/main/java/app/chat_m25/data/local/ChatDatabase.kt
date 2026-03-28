package app.chat_m25.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import app.chat_m25.data.local.dao.ChatSessionDao
import app.chat_m25.data.local.dao.ContactDao
import app.chat_m25.data.local.dao.MessageDao
import app.chat_m25.data.local.dao.MomentDao
import app.chat_m25.data.local.entity.ChatSessionEntity
import app.chat_m25.data.local.entity.ContactEntity
import app.chat_m25.data.local.entity.MessageEntity
import app.chat_m25.data.local.entity.MomentEntity

@Database(
    entities = [
        ChatSessionEntity::class,
        MessageEntity::class,
        ContactEntity::class,
        MomentEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatSessionDao(): ChatSessionDao
    abstract fun messageDao(): MessageDao
    abstract fun contactDao(): ContactDao
    abstract fun momentDao(): MomentDao
}
