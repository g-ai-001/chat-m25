package app.chat_m25.ui.screens.group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.chat_m25.ui.components.Avatar
import app.chat_m25.ui.components.CommonTopBar

@Composable
fun GroupInfoScreen(
    onBack: () -> Unit,
    viewModel: GroupInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "群聊信息",
                onBackClick = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Group avatar and name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    name = uiState.chatSession?.name ?: "群",
                    size = 64.dp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.chatSession?.name ?: "群聊",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (uiState.chatSession?.isGroup == true) "群聊" else "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()

            // Edit group name
            if (uiState.chatSession?.isGroup == true) {
                ListItemWithAction(
                    icon = Icons.Default.Group,
                    title = "群聊名称",
                    subtitle = uiState.chatSession?.name ?: "",
                    onClick = { viewModel.showEditNameDialog() }
                )

                HorizontalDivider()

                // Edit group announcement
                ListItemWithAction(
                    icon = Icons.Default.Campaign,
                    title = "群公告",
                    subtitle = uiState.chatSession?.groupAnnouncement?.take(50)?.let {
                        if (uiState.chatSession?.groupAnnouncement?.length ?: 0 > 50) "$it..." else it
                    } ?: "未设置",
                    onClick = { viewModel.showEditAnnouncementDialog() }
                )

                HorizontalDivider()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "群成员",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Text(
                text = "功能开发中...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Edit name dialog
        if (uiState.showEditNameDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideEditNameDialog() },
                title = { Text("修改群聊名称") },
                text = {
                    OutlinedTextField(
                        value = uiState.editName,
                        onValueChange = { viewModel.updateEditName(it) },
                        label = { Text("群聊名称") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.saveGroupName() },
                        enabled = uiState.editName.isNotBlank() && !uiState.isSaving
                    ) {
                        Text("保存")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideEditNameDialog() }) {
                        Text("取消")
                    }
                }
            )
        }

        // Edit announcement dialog
        if (uiState.showEditAnnouncementDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideEditAnnouncementDialog() },
                title = { Text("修改群公告") },
                text = {
                    OutlinedTextField(
                        value = uiState.editAnnouncement,
                        onValueChange = { viewModel.updateEditAnnouncement(it) },
                        label = { Text("群公告") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.saveGroupAnnouncement() },
                        enabled = !uiState.isSaving
                    ) {
                        Text("保存")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideEditAnnouncementDialog() }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
private fun ListItemWithAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "编辑",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}