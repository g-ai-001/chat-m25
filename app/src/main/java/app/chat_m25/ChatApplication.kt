package app.chat_m25

import android.app.Application
import app.chat_m25.domain.repository.FileLogger
import app.chat_m25.domain.repository.LogLevel

class ChatApplication : Application() {

    companion object {
        lateinit var instance: ChatApplication
            private set
    }

    private lateinit var logger: FileLogger

    override fun onCreate() {
        super.onCreate()
        instance = this
        logger = FileLogger(this)
        logger.log("应用启动")
        logger.log("设备信息: Android ${android.os.Build.VERSION.RELEASE}", LogLevel.INFO)
    }

    fun log(message: String, level: LogLevel = LogLevel.INFO, throwable: Throwable? = null) {
        logger.log(message, level, throwable)
    }

    fun logDebug(message: String) = log(message, LogLevel.DEBUG)
    fun logInfo(message: String) = log(message, LogLevel.INFO)
    fun logWarn(message: String) = log(message, LogLevel.WARN)
    fun logError(message: String, throwable: Throwable? = null) = log(message, LogLevel.ERROR, throwable)

    fun getLogFilePath(): String = logger.getLogFilePath()

    fun getLogContent(): String = logger.getLogContent()
}
