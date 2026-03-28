package app.chat_m25.ui.screens.home

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

data class HomeUiState(
    val sessions: List<ChatSession> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val isSearchMode: Boolean = false,
    val searchResults: List<Message> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            chatRepository.getAllSessions().collect { sessions ->
                _uiState.value = _uiState.value.copy(
                    sessions = sessions,
                    isLoading = false
                )
            }
        }
    }

    fun createDemoSession() {
        viewModelScope.launch {
            val demoChats = listOf(
                Triple("张三", "好的，明天见！", 1),
                Triple("李四", "在吗？", 2),
                Triple("家人群", "晚上吃什么？", 5),
                Triple("工作群", "明天开会", 0),
                Triple("王五", "[图片]", 3)
            )
            demoChats.forEachIndexed { index, (name, lastMsg, unread) ->
                chatRepository.createSession(name).also { chatId ->
                    if (index == 0) {
                        chatRepository.sendMessage(chatId, lastMsg)
                    }
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isNotBlank()) {
            searchMessages(query)
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearchMode = false)
        }
    }

    fun enterSearchMode() {
        _uiState.value = _uiState.value.copy(isSearchMode = true)
    }

    fun exitSearchMode() {
        _uiState.value = _uiState.value.copy(
            isSearchMode = false,
            searchQuery = "",
            searchResults = emptyList()
        )
    }

    private fun searchMessages(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearchMode = true)
            chatRepository.searchMessages(query).collect { messages ->
                _uiState.value = _uiState.value.copy(searchResults = messages)
            }
        }
    }
}
