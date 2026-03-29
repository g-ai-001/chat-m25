package app.chat_m25.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPath: String? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _recordingDuration = MutableStateFlow(0)
    val recordingDuration: StateFlow<Int> = _recordingDuration.asStateFlow()

    private val _playingMessageId = MutableStateFlow<Long?>(null)
    val playingMessageId: StateFlow<Long?> = _playingMessageId.asStateFlow()

    private var recordingStartTime: Long = 0

    fun startRecording(): String? {
        val audioDir = File(context.getExternalFilesDir(null), "audio")
        if (!audioDir.exists()) {
            audioDir.mkdirs()
        }
        val audioFile = File(audioDir, "recording_${System.currentTimeMillis()}.m4a")

        try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }
            _isRecording.value = true
            recordingStartTime = System.currentTimeMillis()
            return audioFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun stopRecording(): Pair<String, Int>? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            _isRecording.value = false
            val duration = ((System.currentTimeMillis() - recordingStartTime) / 1000).toInt()
            _recordingDuration.value = 0
            val audioDir = File(context.getExternalFilesDir(null), "audio")
            val files = audioDir.listFiles()?.sortedByDescending { it.lastModified() }
            files?.firstOrNull()?.let { Pair(it.absolutePath, duration) }
        } catch (e: Exception) {
            e.printStackTrace()
            _isRecording.value = false
            null
        }
    }

    fun cancelRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            _isRecording.value = false
            _recordingDuration.value = 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playAudio(messageId: Long, audioPath: String, onCompletion: () -> Unit = {}) {
        stopPlayback()

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioPath)
                prepare()
                setOnCompletionListener {
                    _isPlaying.value = false
                    _playingMessageId.value = null
                    onCompletion()
                }
                start()
            }
            currentPlayingPath = audioPath
            _isPlaying.value = true
            _playingMessageId.value = messageId
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        currentPlayingPath = null
        _isPlaying.value = false
        _playingMessageId.value = null
    }

    fun isPlayingMessage(messageId: Long): Boolean {
        return _playingMessageId.value == messageId && _isPlaying.value
    }

    fun getRecordingDurationSeconds(): Int {
        return if (_isRecording.value) {
            ((System.currentTimeMillis() - recordingStartTime) / 1000).toInt()
        } else {
            0
        }
    }

    fun release() {
        mediaRecorder?.release()
        mediaPlayer?.release()
        mediaRecorder = null
        mediaPlayer = null
        _isRecording.value = false
        _isPlaying.value = false
    }
}
