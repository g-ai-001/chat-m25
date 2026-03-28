package app.chat_m25

import android.app.Application
import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatApplication : Application() {

    companion object {
        private const val LOG_FILE_NAME = "chat_m25_log.txt"
        private const val MAX_LOG_SIZE = 5 * 1024 * 1024 // 5MB

        lateinit var instance: ChatApplication
            private set
    }

    private lateinit var logFile: File

    override fun onCreate() {
        super.onCreate()
        instance = this
        initLogger()
        log("应用启动")
    }

    private fun initLogger() {
        val logDir = getExternalFilesDir(null) ?: filesDir
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        logFile = File(logDir, LOG_FILE_NAME)

        if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
            logFile.delete()
            logFile.createNewFile()
        }
    }

    fun log(message: String, level: LogLevel = LogLevel.INFO) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val logMessage = "[$timestamp] [${level.name}] $message\n"

        try {
            logFile.appendText(logMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLogFilePath(): String = logFile.absolutePath

    enum class LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}

fun Context.log(message: String, level: ChatApplication.LogLevel = ChatApplication.LogLevel.INFO) {
    ChatApplication.instance.log(message, level)
}
