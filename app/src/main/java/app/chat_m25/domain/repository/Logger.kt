package app.chat_m25.domain.repository

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

interface Logger {
    fun log(message: String, level: LogLevel = LogLevel.INFO, throwable: Throwable? = null)
    fun logDebug(message: String) = log(message, LogLevel.DEBUG)
    fun logInfo(message: String) = log(message, LogLevel.INFO)
    fun logWarn(message: String) = log(message, LogLevel.WARN)
    fun logError(message: String, throwable: Throwable? = null) = log(message, LogLevel.ERROR, throwable)
    fun getLogFilePath(): String
    fun getLogContent(): String
}

@Singleton
class FileLogger @Inject constructor(
    private val context: Context
) : Logger {
    companion object {
        private const val LOG_FILE_NAME = "chat_m25_log.txt"
        private const val MAX_LOG_SIZE = 5 * 1024 * 1024
    }

    private lateinit var logFile: File

    init {
        initLogger()
    }

    private fun initLogger() {
        val logDir = context.getExternalFilesDir(null) ?: context.filesDir
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        logFile = File(logDir, LOG_FILE_NAME)

        if (logFile.exists() && logFile.length() > MAX_LOG_SIZE) {
            logFile.delete()
            logFile.createNewFile()
        }
    }

    override fun log(message: String, level: LogLevel, throwable: Throwable?) {
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

    override fun getLogFilePath(): String = logFile.absolutePath

    override fun getLogContent(): String {
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
}

enum class LogLevel {
    DEBUG, INFO, WARN, ERROR
}
