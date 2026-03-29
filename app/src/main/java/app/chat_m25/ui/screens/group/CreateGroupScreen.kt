package app.chat_m25.ui.screens.group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.chat_m25.domain.model.Contact
import app.chat_m25.ui.components.Avatar
import app.chat_m25.ui.components.CommonTopBar

@Composable
fun CreateGroupScreen(
    onBack: () -> Unit,
    onGroupCreated: (Long) -> Unit,
    viewModel: CreateGroupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var groupName by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        CommonTopBar(
            title = "创建群聊",
            onBackClick = onBack
        )

        OutlinedTextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("群聊名称") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )

        Text(
            text = "选择联系人 (${uiState.selectedContacts.size}人)",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(uiState.contacts) { contact ->
                ContactSelectItem(
                    contact = contact,
                    isSelected = uiState.selectedContacts.contains(contact),
                    onToggle = { viewModel.toggleContact(contact) }
                )
            }
        }

        Button(
            onClick = {
                if (groupName.isNotBlank() && uiState.selectedContacts.isNotEmpty()) {
                    viewModel.createGroup(groupName, uiState.selectedContacts)
                    onGroupCreated(0L)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = groupName.isNotBlank() && uiState.selectedContacts.isNotEmpty()
        ) {
            Icon(Icons.Default.GroupAdd, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("创建群聊")
        }
    }
}

@Composable
fun ContactSelectItem(
    contact: Contact,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(name = contact.name, size = 40.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge
            )
            if (contact.remark.isNotBlank()) {
                Text(
                    text = contact.remark,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() }
        )
    }
}
