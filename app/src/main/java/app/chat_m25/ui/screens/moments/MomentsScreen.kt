package app.chat_m25.ui.screens.moments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.chat_m25.domain.model.Comment
import app.chat_m25.domain.model.Moment
import app.chat_m25.ui.components.Avatar
import app.chat_m25.ui.components.CommonTopBar
import app.chat_m25.ui.components.DateTimeFormatter
import app.chat_m25.ui.components.EmptyState

@Composable
fun MomentsScreen(
    onBack: () -> Unit,
    viewModel: MomentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "朋友圈",
                onBackClick = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showPublishDialog() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "发布")
            }
        }
    ) { paddingValues ->
        if (uiState.moments.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = "暂无朋友圈动态",
                    subtitle = "点击上方+发布朋友圈"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(uiState.moments, key = { it.id }) { moment ->
                    MomentItem(
                        moment = moment,
                        comments = uiState.comments[moment.id] ?: emptyList(),
                        onLikeClick = { viewModel.toggleLike(moment.id) },
                        onDeleteClick = { viewModel.deleteMoment(moment.id) },
                        onCommentClick = { viewModel.showCommentDialog(moment.id) },
                        onDeleteComment = { viewModel.deleteComment(it) }
                    )
                }
            }
        }

        if (uiState.showPublishDialog) {
            PublishMomentDialog(
                content = uiState.publishContent,
                images = uiState.publishImages,
                isPublishing = uiState.isPublishing,
                onContentChange = { viewModel.updatePublishContent(it) },
                onImagesChange = { viewModel.updatePublishImages(it) },
                onDismiss = { viewModel.hidePublishDialog() },
                onPublish = { viewModel.publish() }
            )
        }

        if (uiState.showCommentDialog && uiState.selectedMomentId != null) {
            CommentDialog(
                comments = uiState.comments[uiState.selectedMomentId] ?: emptyList(),
                commentContent = uiState.commentContent,
                onContentChange = { viewModel.updateCommentContent(it) },
                onDismiss = { viewModel.hideCommentDialog() },
                onSend = { viewModel.addComment(uiState.selectedMomentId!!) },
                onDeleteComment = { viewModel.deleteComment(it) }
            )
        }
    }
}

@Composable
fun MomentItem(
    moment: Moment,
    comments: List<Comment>,
    onLikeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCommentClick: () -> Unit,
    onDeleteComment: (Long) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Avatar(
                    name = moment.userName,
                    size = 44.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = moment.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = moment.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (moment.images.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        MomentImages(images = moment.images)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = DateTimeFormatter.formatMomentTime(moment.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onLikeClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (moment.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "点赞",
                                    tint = if (moment.isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = onCommentClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Comment,
                                    contentDescription = "评论",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box {
                                IconButton(
                                    onClick = { showMenu = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "更多",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("删除") },
                                        onClick = {
                                            showMenu = false
                                            onDeleteClick()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (comments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CommentSection(
                            comments = comments,
                            onDeleteComment = onDeleteComment
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MomentImages(images: List<String>) {
    when (images.size) {
        1 -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = "图片",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else -> {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(images) { _ ->
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "图片",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PublishMomentDialog(
    content: String,
    images: List<String>,
    isPublishing: Boolean,
    onContentChange: (String) -> Unit,
    onImagesChange: (List<String>) -> Unit,
    onDismiss: () -> Unit,
    onPublish: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isPublishing) onDismiss() },
        title = { Text("发布朋友圈") },
        text = {
            Column {
                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("分享你的想法...") },
                    maxLines = 5,
                    enabled = !isPublishing
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { },
                        enabled = !isPublishing
                    ) {
                        Icon(
                            Icons.Default.Photo,
                            contentDescription = "添加图片",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (isPublishing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onPublish,
                enabled = !isPublishing && (content.isNotBlank() || images.isNotEmpty())
            ) {
                Text("发布")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isPublishing
            ) {
                Text("取消")
            }
        }
    )
}

@Composable
fun CommentSection(
    comments: List<Comment>,
    onDeleteComment: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(4.dp)
            )
            .padding(8.dp)
    ) {
        comments.forEach { comment ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (comment.replyToUserName != null) {
                    Text(
                        text = " 回复 ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = comment.replyToUserName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = ": ${comment.content}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun CommentDialog(
    comments: List<Comment>,
    commentContent: String,
    onContentChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSend: () -> Unit,
    onDeleteComment: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("评论") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                if (comments.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(comments) { comment ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = comment.userName,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = comment.content,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = DateTimeFormatter.formatMomentTime(comment.timestamp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (comment.userId == 1L) {
                                    IconButton(
                                        onClick = { onDeleteComment(comment.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.MoreVert,
                                            contentDescription = "删除",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无评论",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentContent,
                        onValueChange = onContentChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("写评论...") },
                        maxLines = 3,
                        enabled = true
                    )
                    IconButton(
                        onClick = onSend,
                        enabled = commentContent.isNotBlank()
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "发送",
                            tint = if (commentContent.isNotBlank())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}