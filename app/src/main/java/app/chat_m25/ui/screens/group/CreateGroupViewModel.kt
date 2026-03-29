package app.chat_m25.ui.screens.group

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

data class CreateGroupUiState(
    val contacts: List<Contact> = emptyList(),
    val selectedContacts: List<Contact> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CreateGroupViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateGroupUiState())
    val uiState: StateFlow<CreateGroupUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            contactRepository.getAllContacts().collect { contacts ->
                _uiState.value = _uiState.value.copy(
                    contacts = contacts,
                    isLoading = false
                )
            }
        }
    }

    fun toggleContact(contact: Contact) {
        val currentSelected = _uiState.value.selectedContacts.toMutableList()
        if (currentSelected.contains(contact)) {
            currentSelected.remove(contact)
        } else {
            currentSelected.add(contact)
        }
        _uiState.value = _uiState.value.copy(selectedContacts = currentSelected)
    }

    fun createGroup(name: String, members: List<Contact>) {
        viewModelScope.launch {
            val memberIds = members.map { it.id }
            chatRepository.createGroup(name, memberIds)
        }
    }
}
