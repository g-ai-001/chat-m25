package app.chat_m25.ui.screens.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.chat_m25.data.repository.ContactRepository
import app.chat_m25.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactsUiState(
    val contacts: List<Contact> = emptyList(),
    val starredContacts: List<Contact> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = ""
)

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

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
        viewModelScope.launch {
            contactRepository.getStarredContacts().collect { starred ->
                _uiState.value = _uiState.value.copy(
                    starredContacts = starred
                )
            }
        }
    }

    fun addDemoContacts() {
        viewModelScope.launch {
            val demoContacts = listOf(
                Triple("张三", "13800138001", "同事"),
                Triple("李四", "13800138002", "朋友"),
                Triple("王五", "13800138003", "家人"),
                Triple("赵六", "13800138004", "老板")
            )
            demoContacts.forEach { (name, phone, remark) ->
                contactRepository.addContact(name, phone, remark)
            }
        }
    }

    fun search(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            loadContacts()
        } else {
            viewModelScope.launch {
                contactRepository.searchContacts(query).collect { contacts ->
                    _uiState.value = _uiState.value.copy(contacts = contacts)
                }
            }
        }
    }
}
