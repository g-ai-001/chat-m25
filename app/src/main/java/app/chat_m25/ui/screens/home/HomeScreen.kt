package app.chat_m25.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.chat_m25.domain.model.ChatSession
import app.chat_m25.domain.model.Message
import app.chat_m25.ui.components.Avatar
import app.chat_m25.ui.components.DateTimeFormatter
import app.chat_m25.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onChatClick: (Long) -> Unit,
    onMomentsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            if (uiState.isSearchMode) {
                SearchTopBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onBack = { viewModel.exitSearchMode() }
                )
            } else {
                TopAppBar(
                    title = { Text("微信", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    actions = {
                        IconButton(onClick = { viewModel.enterSearchMode() }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "搜索",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = onMomentsClick) {
                            Icon(
                                Icons.Filled.People,
                                contentDescription = "朋友圈",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = { viewModel.createDemoSession() }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "新建聊天",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            if (!uiState.isSearchMode) {
                FloatingActionButton(
                    onClick = { viewModel.createDemoSession() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "新建")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isSearchMode) {
                SearchContent(
                    results = uiState.searchResults,
                    onResultClick = { message ->
                        onChatClick(message.chatId)
                    }
                )
            } else {
                ChatListContent(
                    sessions = uiState.sessions,
                    isLoading = uiState.isLoading,
                    onChatClick = onChatClick,
                    onCreateDemo = { viewModel.createDemoSession() },
                    onTogglePin = { viewModel.togglePin(it) },
                    onToggleDoNotDisturb = { viewModel.toggleDoNotDisturb(it) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                "搜索聊天记录",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun SearchContent(
    results: List<Message>,
    onResultClick: (Message) -> Unit
) {
    if (results.isEmpty()) {
        EmptyState(
            title = "暂无搜索结果",
            subtitle = "尝试输入其他关键词"
        )
    } else {
        LazyColumn {
            items(results, key = { it.id }) { message ->
                SearchResultItem(
                    message = message,
                    onClick = { onResultClick(message) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun SearchResultItem(
    message: Message,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            name = if (message.isFromMe) "我" else "?",
            size = 40.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = DateTimeFormatter.formatFullDateTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChatListContent(
    sessions: List<ChatSession>,
    isLoading: Boolean,
    onChatClick: (Long) -> Unit,
    onCreateDemo: () -> Unit,
    onTogglePin: (ChatSession) -> Unit,
    onToggleDoNotDisturb: (ChatSession) -> Unit
) {
    if (sessions.isEmpty() && !isLoading) {
        EmptyState(
            title = "暂无聊天记录",
            subtitle = "点击上方+创建演示数据"
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(sessions, key = { it.id }) { session ->
                ChatSessionItem(
                    session = session,
                    onClick = { onChatClick(session.id) },
                    onTogglePin = { onTogglePin(session) },
                    onToggleDoNotDisturb = { onToggleDoNotDisturb(session) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 72.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatSessionItem(
    session: ChatSession,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleDoNotDisturb: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            name = session.name,
            size = 48.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (session.isPinned) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "已置顶",
                            modifier = Modifier.padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (session.doNotDisturb) {
                        Icon(
                            Icons.Default.NotificationsOff,
                            contentDescription = "免打扰",
                            modifier = Modifier.padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = session.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = DateTimeFormatter.formatChatTime(session.lastMessageTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (session.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge {
                        Text(
                            text = if (session.unreadCount > 99) "99+" else session.unreadCount.toString(),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(if (session.isPinned) "取消置顶" else "置顶聊天") },
                onClick = {
                    onTogglePin()
                    showMenu = false
                },
                leadingIcon = {
                    Icon(Icons.Default.PushPin, contentDescription = null)
                }
            )
            DropdownMenuItem(
                text = { Text(if (session.doNotDisturb) "取消免打扰" else "消息免打扰") },
                onClick = {
                    onToggleDoNotDisturb()
                    showMenu = false
                },
                leadingIcon = {
                    Icon(Icons.Default.NotificationsOff, contentDescription = null)
                }
            )
        }
    }
}