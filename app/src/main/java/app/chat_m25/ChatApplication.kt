package app.chat_m25

import android.app.Application
import android.content.Context
import app.chat_m25.domain.repository.FileLogger
import app.chat_m25.domain.repository.LogLevel
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatApplication : Application() {

    companion object {
        lateinit var instance: ChatApplication
            private set
    }

    private lateinit var logger: FileLogger

    override fun onCreate() {
        super.onCreate()
        // 先设置instance，确保即使后续初始化失败也能访问
        instance = this
        // 初始化日志系统，使用try-catch防止崩溃
        try {
            logger = FileLogger(this)
            logger.log("应用启动")
            logger.log("设备信息: Android ${android.os.Build.VERSION.RELEASE}", LogLevel.INFO)
        } catch (e: Exception) {
            e.printStackTrace()
            // 创建一个最小化的logger用于紧急日志
            logger = FileLogger(this, minimal = true)
        }
    }

    fun log(message: String, level: LogLevel = LogLevel.INFO, throwable: Throwable? = null) {
        try {
            logger.log(message, level, throwable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logDebug(message: String) = log(message, LogLevel.DEBUG)
    fun logInfo(message: String) = log(message, LogLevel.INFO)
    fun logWarn(message: String) = log(message, LogLevel.WARN)
    fun logError(message: String, throwable: Throwable? = null) = log(message, LogLevel.ERROR, throwable)

    fun getLogFilePath(): String = logger.getLogFilePath()

    fun getLogContent(): String = logger.getLogContent()
}

// Backward compatibility extension functions
fun Context.log(message: String, level: LogLevel = LogLevel.INFO) {
    if (::instance.isInitialized) {
        instance.log(message, level)
    }
}
fun Context.logDebug(message: String) = log(message, LogLevel.DEBUG)
fun Context.logInfo(message: String) = log(message, LogLevel.INFO)
fun Context.logWarn(message: String) = log(message, LogLevel.WARN)
fun Context.logError(message: String, throwable: Throwable? = null) {
    if (::instance.isInitialized) {
        instance.log(message, LogLevel.ERROR, throwable)
    }
}
