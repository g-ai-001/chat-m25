package app.chat_m25.ui.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.chat_m25.data.repository.ChatRepository
import app.chat_m25.domain.model.ChatSession
import app.chat_m25.domain.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatDetailUiState(
    val chatSession: ChatSession? = null,
    val messages: List<Message> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = true,
    val showEmojiPicker: Boolean = false
)

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val chatId: Long = savedStateHandle.get<Long>("chatId") ?: 0L

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    init {
        loadChat()
        loadMessages()
        markAsRead()
    }

    private fun loadChat() {
        viewModelScope.launch {
            val session = chatRepository.getSessionById(chatId)
            _uiState.value = _uiState.value.copy(chatSession = session)
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            chatRepository.getMessages(chatId).collect { messages ->
                _uiState.value = _uiState.value.copy(
                    messages = messages,
                    isLoading = false
                )
            }
        }
    }

    private fun markAsRead() {
        viewModelScope.launch {
            chatRepository.markAsRead(chatId)
        }
    }

    fun updateInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            chatRepository.sendMessage(chatId, text)
            _uiState.value = _uiState.value.copy(inputText = "")
        }
    }

    fun toggleEmojiPicker() {
        _uiState.value = _uiState.value.copy(showEmojiPicker = !_uiState.value.showEmojiPicker)
    }

    fun onEmojiSelected(emoji: String) {
        _uiState.value = _uiState.value.copy(
            inputText = _uiState.value.inputText + emoji,
            showEmojiPicker = false
        )
    }

    fun hideEmojiPicker() {
        _uiState.value = _uiState.value.copy(showEmojiPicker = false)
    }

    fun addDemoMessages() {
        viewModelScope.launch {
            val demoMessages = listOf(
                "你好！",
                "在吗？",
                "晚上一起吃饭吧",
                "好的，几点？",
                "七点怎么样？",
                "可以，到时候见"
            )
            demoMessages.forEachIndexed { index, content ->
                val isFromMe = index % 2 == 0
                if (isFromMe) {
                    chatRepository.sendMessage(chatId, content)
                }
            }
        }
    }
}
