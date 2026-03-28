package app.chat_m25.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.chat_m25.data.local.ChatDatabase
import app.chat_m25.data.local.dao.ChatSessionDao
import app.chat_m25.data.local.dao.ContactDao
import app.chat_m25.data.local.dao.MessageDao
import app.chat_m25.data.local.dao.MomentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add migrations if needed
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context,
            ChatDatabase::class.java,
            "chat_m25_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideChatSessionDao(database: ChatDatabase): ChatSessionDao {
        return database.chatSessionDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: ChatDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Singleton
    fun provideContactDao(database: ChatDatabase): ContactDao {
        return database.contactDao()
    }

    @Provides
    @Singleton
    fun provideMomentDao(database: ChatDatabase): MomentDao {
        return database.momentDao()
    }
}
