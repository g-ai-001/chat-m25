package app.chat_m25.di

import android.content.Context
import app.chat_m25.domain.repository.FileLogger
import app.chat_m25.domain.repository.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {

    @Provides
    @Singleton
    fun provideLogger(@ApplicationContext context: Context): Logger {
        return FileLogger(context)
    }
}
