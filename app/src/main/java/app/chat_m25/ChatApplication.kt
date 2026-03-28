package app.chat_m25

import android.app.Application
import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
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
        log("设备信息: Android ${android.os.Build.VERSION.RELEASE}", LogLevel.INFO)
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

    fun log(message: String, level: LogLevel = LogLevel.INFO, throwable: Throwable? = null) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        val threadName = Thread.currentThread().name
        val stackTrace = throwable?.let { getStackTrace(it) } ?: ""

        val logMessage = buildString {
            append("[$timestamp] [${level.name}] [$threadName] $message")
            if (stackTrace.isNotEmpty()) {
                append("\n$stackTrace")
            }
            append("\n")
        }

        try {
            logFile.appendText(logMessage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logDebug(message: String) = log(message, LogLevel.DEBUG)

    fun logInfo(message: String) = log(message, LogLevel.INFO)

    fun logWarn(message: String) = log(message, LogLevel.WARN)

    fun logError(message: String, throwable: Throwable? = null) = log(message, LogLevel.ERROR, throwable)

    fun getLogFilePath(): String = logFile.absolutePath

    fun getLogContent(): String {
        return try {
            logFile.readText()
        } catch (e: Exception) {
            "无法读取日志文件: ${e.message}"
        }
    }

    private fun getStackTrace(throwable: Throwable): String {
        val stringWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stringWriter))
        return stringWriter.toString()
    }

    enum class LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}

fun Context.log(message: String, level: ChatApplication.LogLevel = ChatApplication.LogLevel.INFO) {
    ChatApplication.instance.log(message, level)
}

fun Context.logDebug(message: String) = ChatApplication.instance.logDebug(message)
fun Context.logInfo(message: String) = ChatApplication.instance.logInfo(message)
fun Context.logWarn(message: String) = ChatApplication.instance.logWarn(message)
fun Context.logError(message: String, throwable: Throwable? = null) = ChatApplication.instance.logError(message, throwable)
