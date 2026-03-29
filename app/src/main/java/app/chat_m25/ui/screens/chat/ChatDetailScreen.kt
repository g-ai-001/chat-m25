package app.chat_m25.ui.screens.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.chat_m25.domain.model.Message
import app.chat_m25.ui.components.Avatar
import app.chat_m25.ui.components.CommonTopBar
import app.chat_m25.ui.components.DateTimeFormatter
import app.chat_m25.ui.components.EmptyState
import app.chat_m25.ui.components.EmojiPicker

@Composable
fun ChatDetailScreen(
    chatId: Long,
    onBack: () -> Unit,
    onGroupInfoClick: (Long) -> Unit = {},
    viewModel: ChatDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showMenu by remember { mutableStateOf(false) }
    var showLocationPicker by remember { mutableStateOf(false) }
    var showMorePanel by remember { mutableStateOf(false) }
    val backgroundColor = uiState.chatSession?.backgroundColor?.let { Color(it) } ?: Color.White
    val isRecording by viewModel.isRecording.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playingMessageId by viewModel.playingMessageId.collectAsState()
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val fileName = uri.lastPathSegment ?: "文件"
            val filePath = uri.toString()
            viewModel.sendFile(fileName, filePath, 0)
        }
        showMorePanel = false
    }

    LaunchedEffect(uiState.scrollToMessageId) {
        uiState.scrollToMessageId?.let { messageId ->
            val index = uiState.messages.indexOfFirst { it.id == messageId }
            if (index >= 0) {
                listState.animateScrollToItem(index)
                viewModel.clearScrollToMessage()
            }
        }
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty() && uiState.scrollToMessageId == null) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                title = uiState.chatSession?.name ?: "聊天",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { viewModel.addDemoMessages() }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "更多",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "更多",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (uiState.chatSession?.isGroup == true) {
                                DropdownMenuItem(
                                    text = { Text("群聊信息") },
                                    onClick = {
                                        showMenu = false
                                        onGroupInfoClick(chatId)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.Info, contentDescription = null)
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("设置聊天背景") },
                                onClick = {
                                    viewModel.toggleBackgroundPicker()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ColorLens, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        title = "暂无消息",
                        subtitle = "点击上方+发送演示消息"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(backgroundColor),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    items(uiState.messages, key = { it.id }) { message ->
                        when (message.mediaType) {
                            "AUDIO" -> VoiceMessageBubble(
                                message = message,
                                isPlaying = isPlaying && playingMessageId == message.id,
                                onPlay = { viewModel.playAudio(message) },
                                onDelete = { viewModel.deleteMessage(it) },
                                onToggleFavorite = { id, isFav -> viewModel.toggleFavorite(id, isFav) },
                                onReply = { viewModel.replyToMessage(it) },
                                onForward = { viewModel.showForwardDialog(it) },
                                onRecall = { viewModel.recallMessage(it) }
                            )
                            "LOCATION" -> LocationMessageBubble(
                                message = message,
                                onDelete = { viewModel.deleteMessage(it) },
                                onToggleFavorite = { id, isFav -> viewModel.toggleFavorite(id, isFav) },
                                onReply = { viewModel.replyToMessage(it) },
                                onForward = { viewModel.showForwardDialog(it) },
                                onRecall = { viewModel.recallMessage(it) }
                            )
                            "FILE" -> FileMessageBubble(
                                message = message,
                                onDelete = { viewModel.deleteMessage(it) },
                                onToggleFavorite = { id, isFav -> viewModel.toggleFavorite(id, isFav) },
                                onReply = { viewModel.replyToMessage(it) },
                                onForward = { viewModel.showForwardDialog(it) },
                                onRecall = { viewModel.recallMessage(it) }
                            )
                            else -> MessageBubble(
                                message = message,
                                onDelete = { viewModel.deleteMessage(it) },
                                onToggleFavorite = { id, isFav -> viewModel.toggleFavorite(id, isFav) },
                                onReply = { viewModel.replyToMessage(it) },
                                onForward = { viewModel.showForwardDialog(it) },
                                onRecall = { viewModel.recallMessage(it) },
                                onJumpToMessage = { viewModel.jumpToMessage(it) },
                                replyToMessage = message.replyToId?.let { uiState.replyToMessages[it] }
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.showBackgroundPicker,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                BackgroundPicker(
                    currentColor = backgroundColor,
                    onColorSelected = { viewModel.updateBackgroundColor(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(visible = uiState.replyingTo != null) {
                uiState.replyingTo?.let { replyMessage ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Reply,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "回复 ${replyMessage.content.take(20)}${if (replyMessage.content.length > 20) "..." else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { viewModel.cancelReply() }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "取消回复",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (isRecording) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "录音中... 向上滑动取消",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        TextButton(onClick = { viewModel.cancelRecording() }) {
                            Text("取消")
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showMorePanel = !showMorePanel }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "更多",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { viewModel.toggleEmojiPicker() }) {
                    Icon(
                        Icons.Default.EmojiEmotions,
                        contentDescription = "表情",
                        tint = if (uiState.showEmojiPicker)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedTextField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.updateInputText(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("输入消息...") },
                    maxLines = 6,
                    minLines = 1,
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = androidx.compose.foundation.text.input.KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Default
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (uiState.inputText.isNotBlank()) {
                    IconButton(
                        onClick = {
                            if (uiState.replyingTo != null) {
                                viewModel.sendReplyMessage()
                            } else {
                                viewModel.sendMessage()
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "发送",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    IconButton(
                        onClick = { viewModel.startRecording() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            Icons.Default.Mic,
                            contentDescription = "录音",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { viewModel.stopRecordingAndSend() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "发送语音",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = uiState.showEmojiPicker,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                EmojiPicker(
                    onEmojiSelected = { viewModel.onEmojiSelected(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AnimatedVisibility(
                visible = showMorePanel,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                showLocationPicker = true
                                showMorePanel = false
                            }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = "位置",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "位置",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                filePickerLauncher.launch("*/*")
                            }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Folder,
                                        contentDescription = "文件",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "文件",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            if (uiState.showForwardDialog && uiState.forwardMessage != null) {
                ForwardDialog(
                    message = uiState.forwardMessage!!,
                    onDismiss = { viewModel.hideForwardDialog() },
                    onForward = { targetChatId -> viewModel.forwardMessageTo(targetChatId) }
                )
            }

            if (showLocationPicker) {
                LocationPickerDialog(
                    onDismiss = { showLocationPicker = false },
                    onLocationSelected = { lat, lng, address ->
                        viewModel.sendLocation(lat, lng, address)
                        showLocationPicker = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VoiceMessageBubble(
    message: Message,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onDelete: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onReply: (Message) -> Unit,
    onForward: (Message) -> Unit,
    onRecall: (Long) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isFromMe) {
                Avatar(
                    name = "?",
                    size = 36.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box {
                Box(
                    modifier = Modifier
                        .widthIn(max = 200.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (message.isFromMe) 16.dp else 4.dp,
                                bottomEnd = if (message.isFromMe) 4.dp else 16.dp
                            )
                        )
                        .background(
                            if (message.isFromMe)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .combinedClickable(
                            onClick = { onPlay() },
                            onLongClick = { showMenu = true }
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "暂停" else "播放",
                            tint = if (message.isFromMe)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "${message.duration}秒",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (message.isFromMe)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (message.isFromMe && message.status.name != "RECALLED") {
                        DropdownMenuItem(
                            text = { Text("撤回") },
                            onClick = {
                                onRecall(message.id)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("回复") },
                        onClick = {
                            onReply(message)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Reply, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("转发") },
                        onClick = {
                            onForward(message)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (message.isFavorite) "取消收藏" else "收藏") },
                        onClick = {
                            onToggleFavorite(message.id, message.isFavorite)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                if (message.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("删除") },
                        onClick = {
                            onDelete(message.id)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )
                }
            }

            if (message.isFromMe) {
                Spacer(modifier = Modifier.width(8.dp))
                Avatar(
                    name = "我",
                    size = 36.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = DateTimeFormatter.formatTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            if (message.isFromMe && message.status.name != "RECALLED") {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (message.status.name) {
                        "SENDING" -> "发送中"
                        "SENT" -> "已发送"
                        "DELIVERED" -> "已送达"
                        "READ" -> "已读"
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocationMessageBubble(
    message: Message,
    onDelete: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onReply: (Message) -> Unit,
    onForward: (Message) -> Unit,
    onRecall: (Long) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isFromMe) {
                Avatar(
                    name = "?",
                    size = 36.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box {
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (message.isFromMe) 16.dp else 4.dp,
                                bottomEnd = if (message.isFromMe) 4.dp else 16.dp
                            )
                        )
                        .background(
                            if (message.isFromMe)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .combinedClickable(
                            onClick = { },
                            onLongClick = { showMenu = true }
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = if (message.isFromMe)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = "位置",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (message.isFromMe)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = message.content.split("\n").firstOrNull() ?: "位置",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (message.isFromMe)
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (message.isFromMe && message.status.name != "RECALLED") {
                        DropdownMenuItem(
                            text = { Text("撤回") },
                            onClick = {
                                onRecall(message.id)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("回复") },
                        onClick = {
                            onReply(message)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Reply, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("转发") },
                        onClick = {
                            onForward(message)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (message.isFavorite) "取消收藏" else "收藏") },
                        onClick = {
                            onToggleFavorite(message.id, message.isFavorite)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                if (message.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("删除") },
                        onClick = {
                            onDelete(message.id)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )
                }
            }

            if (message.isFromMe) {
                Spacer(modifier = Modifier.width(8.dp))
                Avatar(
                    name = "我",
                    size = 36.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = DateTimeFormatter.formatTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            if (message.isFromMe && message.status.name != "RECALLED") {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (message.status.name) {
                        "SENDING" -> "发送中"
                        "SENT" -> "已发送"
                        "DELIVERED" -> "已送达"
                        "READ" -> "已读"
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: Message,
    onDelete: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onReply: (Message) -> Unit,
    onForward: (Message) -> Unit,
    onRecall: (Long) -> Unit,
    onJumpToMessage: (Long) -> Unit = {},
    replyToMessage: Message? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isFromMe) {
                Avatar(
                    name = "?",
                    size = 36.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box {
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (message.isFromMe) 16.dp else 4.dp,
                                bottomEnd = if (message.isFromMe) 4.dp else 16.dp
                            )
                        )
                        .background(
                            if (message.isFromMe)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .combinedClickable(
                            onClick = { },
                            onLongClick = { showMenu = true }
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
                        if (message.replyToId != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { onJumpToMessage(message.replyToId) }
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = replyToMessage?.content?.take(30)?.let {
                                        if (replyToMessage.content.length > 30) "$it..." else it
                                    } ?: "回复消息",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (message.isFromMe)
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = if (message.status.name == "RECALLED") "消息已撤回" else message.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (message.isFromMe)
                                MaterialTheme.colorScheme.onPrimary.copy(alpha = if (message.status.name == "RECALLED") 0.5f else 1f)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (message.status.name == "RECALLED") 0.5f else 1f)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (message.isFromMe && message.status.name != "RECALLED") {
                        DropdownMenuItem(
                            text = { Text("撤回") },
                            onClick = {
                                onRecall(message.id)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("回复") },
                        onClick = {
                            onReply(message)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Reply, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("转发") },
                        onClick = {
                            onForward(message)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (message.isFavorite) "取消收藏" else "收藏") },
                        onClick = {
                            onToggleFavorite(message.id, message.isFavorite)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                if (message.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("删除") },
                        onClick = {
                            onDelete(message.id)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )
                }
            }

            if (message.isFromMe) {
                Spacer(modifier = Modifier.width(8.dp))
                Avatar(
                    name = "我",
                    size = 36.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = DateTimeFormatter.formatTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            if (message.isFromMe && message.status.name != "RECALLED") {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (message.status.name) {
                        "SENDING" -> "发送中"
                        "SENT" -> "已发送"
                        "DELIVERED" -> "已送达"
                        "READ" -> "已读"
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileMessageBubble(
    message: Message,
    onDelete: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onReply: (Message) -> Unit,
    onForward: (Message) -> Unit,
    onRecall: (Long) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isFromMe) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isFromMe) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isFromMe) {
                Avatar(
                    name = "?",
                    size = 36.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box {
                Box(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (message.isFromMe) 16.dp else 4.dp,
                                bottomEnd = if (message.isFromMe) 4.dp else 16.dp
                            )
                        )
                        .background(
                            if (message.isFromMe)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                        .combinedClickable(
                            onClick = { },
                            onLongClick = { showMenu = true }
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = if (message.isFromMe)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Column(modifier = Modifier.widthIn(max = 180.dp)) {
                            Text(
                                text = message.content.split(" (").firstOrNull() ?: "文件",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (message.isFromMe)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                            Text(
                                text = message.content.split("(").getOrNull(1)?.removeSuffix(")") ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (message.isFromMe)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                maxLines = 1
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    if (message.isFromMe && message.status.name != "RECALLED") {
                        DropdownMenuItem(
                            text = { Text("撤回") },
                            onClick = {
                                onRecall(message.id)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.SwapHoriz, contentDescription = null)
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("回复") },
                        onClick = {
                            onReply(message)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Reply, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("转发") },
                        onClick = {
                            onForward(message)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (message.isFavorite) "取消收藏" else "收藏") },
                        onClick = {
                            onToggleFavorite(message.id, message.isFavorite)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                if (message.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("删除") },
                        onClick = {
                            onDelete(message.id)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )
                }
            }

            if (message.isFromMe) {
                Spacer(modifier = Modifier.width(8.dp))
                Avatar(
                    name = "我",
                    size = 36.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = DateTimeFormatter.formatTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            if (message.isFromMe && message.status.name != "RECALLED") {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (message.status.name) {
                        "SENDING" -> "发送中"
                        "SENT" -> "已发送"
                        "DELIVERED" -> "已送达"
                        "READ" -> "已读"
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun BackgroundPicker(
    currentColor: Color,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        0xFFFFFFFF to "默认白",
        0xFFF5F5DC to "米黄",
        0xFFE6E6FA to "淡紫",
        0xFFE0FFFF to "浅蓝",
        0xFFF0FFF0 to "薄荷绿",
        0xFFFFF0F5 to "浅粉",
        0xFFF5F5F5 to "暖灰",
        0xFFFDF5E6 to "旧蕾丝"
    )

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "选择聊天背景",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            colors.forEach { (color, name) ->
                val isSelected = currentColor.value.toLong() == color
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onColorSelected(color) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(color))
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        2.dp,
                                        MaterialTheme.colorScheme.primary,
                                        CircleShape
                                    )
                                } else {
                                    Modifier.border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline,
                                        CircleShape
                                    )
                                }
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ForwardDialog(
    message: Message,
    onDismiss: () -> Unit,
    onForward: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("转发消息") },
        text = {
            Column {
                Text(
                    text = "选择要转发到的聊天：",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "消息内容：${message.content.take(50)}${if (message.content.length > 50) "..." else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "功能开发中，请稍后...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
