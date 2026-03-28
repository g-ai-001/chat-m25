package app.chat_m25.ui.screens.favorites

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

data class FavoritesUiState(
    val favoriteMessages: List<FavoriteMessage> = emptyList(),
    val isLoading: Boolean = true
)

data class FavoriteMessage(
    val message: Message,
    val chatName: String
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            chatRepository.getFavoriteMessages().collect { messages ->
                val favoriteMessages = messages.map { message ->
                    val chatSession = chatRepository.getSessionById(message.chatId)
                    FavoriteMessage(
                        message = message,
                        chatName = chatSession?.name ?: "未知会话"
                    )
                }
                _uiState.value = _uiState.value.copy(
                    favoriteMessages = favoriteMessages,
                    isLoading = false
                )
            }
        }
    }

    fun removeFromFavorites(messageId: Long) {
        viewModelScope.launch {
            chatRepository.toggleFavorite(messageId, false)
        }
    }
}