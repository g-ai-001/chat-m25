package app.chat_m25.ui.screens.contacts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.chat_m25.data.repository.ChatRepository
import app.chat_m25.data.repository.ContactRepository
import app.chat_m25.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactDetailUiState(
    val contact: Contact? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ContactDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val contactRepository: ContactRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val contactId: Long = savedStateHandle.get<Long>("contactId") ?: 0L

    private val _uiState = MutableStateFlow(ContactDetailUiState())
    val uiState: StateFlow<ContactDetailUiState> = _uiState.asStateFlow()

    init {
        loadContact()
    }

    private fun loadContact() {
        viewModelScope.launch {
            val contact = contactRepository.getContactById(contactId)
            _uiState.value = _uiState.value.copy(
                contact = contact,
                isLoading = false
            )
        }
    }

    fun toggleStarred() {
        val contact = _uiState.value.contact ?: return
        viewModelScope.launch {
            contactRepository.toggleStarred(contact.id, !contact.isStarred)
            loadContact()
        }
    }

    suspend fun createChatSession(): Long {
        val contact = _uiState.value.contact ?: return 0L
        return chatRepository.createSession(contact.name, contact.avatar)
    }
}