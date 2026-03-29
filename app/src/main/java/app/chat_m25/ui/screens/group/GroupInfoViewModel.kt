package app.chat_m25.ui.screens.group

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.chat_m25.data.repository.ChatRepository
import app.chat_m25.domain.model.ChatSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupInfoUiState(
    val chatSession: ChatSession? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val editName: String = "",
    val editAnnouncement: String = "",
    val showEditNameDialog: Boolean = false,
    val showEditAnnouncementDialog: Boolean = false
)

@HiltViewModel
class GroupInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val chatId: Long = savedStateHandle.get<Long>("chatId") ?: 0L

    private val _uiState = MutableStateFlow(GroupInfoUiState())
    val uiState: StateFlow<GroupInfoUiState> = _uiState.asStateFlow()

    init {
        loadGroupInfo()
    }

    private fun loadGroupInfo() {
        viewModelScope.launch {
            val session = chatRepository.getSessionById(chatId)
            _uiState.value = _uiState.value.copy(
                chatSession = session,
                editName = session?.name ?: "",
                editAnnouncement = session?.groupAnnouncement ?: "",
                isLoading = false
            )
        }
    }

    fun showEditNameDialog() {
        _uiState.value = _uiState.value.copy(
            showEditNameDialog = true,
            editName = _uiState.value.chatSession?.name ?: ""
        )
    }

    fun hideEditNameDialog() {
        _uiState.value = _uiState.value.copy(showEditNameDialog = false)
    }

    fun updateEditName(name: String) {
        _uiState.value = _uiState.value.copy(editName = name)
    }

    fun saveGroupName() {
        val name = _uiState.value.editName.trim()
        if (name.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            chatRepository.updateGroupName(chatId, name)
            loadGroupInfo()
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                showEditNameDialog = false
            )
        }
    }

    fun showEditAnnouncementDialog() {
        _uiState.value = _uiState.value.copy(
            showEditAnnouncementDialog = true,
            editAnnouncement = _uiState.value.chatSession?.groupAnnouncement ?: ""
        )
    }

    fun hideEditAnnouncementDialog() {
        _uiState.value = _uiState.value.copy(showEditAnnouncementDialog = false)
    }

    fun updateEditAnnouncement(announcement: String) {
        _uiState.value = _uiState.value.copy(editAnnouncement = announcement)
    }

    fun saveGroupAnnouncement() {
        val announcement = _uiState.value.editAnnouncement.trim()

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            chatRepository.updateGroupAnnouncement(chatId, announcement)
            loadGroupInfo()
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                showEditAnnouncementDialog = false
            )
        }
    }
}