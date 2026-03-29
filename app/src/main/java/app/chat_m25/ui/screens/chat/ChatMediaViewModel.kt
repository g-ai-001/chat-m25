package app.chat_m25.ui.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.chat_m25.data.repository.ChatRepository
import app.chat_m25.domain.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMediaUiState(
    val mediaMessages: List<Message> = emptyList(),
    val fileMessages: List<Message> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class ChatMediaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val chatId: Long = savedStateHandle.get<Long>("chatId") ?: 0L

    private val _uiState = MutableStateFlow(ChatMediaUiState())
    val uiState: StateFlow<ChatMediaUiState> = _uiState.asStateFlow()

    init {
        loadMediaMessages()
        loadFileMessages()
    }

    private fun loadMediaMessages() {
        viewModelScope.launch {
            chatRepository.getMediaMessages(chatId).collect { messages ->
                _uiState.value = _uiState.value.copy(
                    mediaMessages = messages,
                    isLoading = false
                )
            }
        }
    }

    private fun loadFileMessages() {
        viewModelScope.launch {
            chatRepository.getFileMessages(chatId).collect { messages ->
                _uiState.value = _uiState.value.copy(
                    fileMessages = messages
                )
            }
        }
    }
}