package app.chat_m25.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmojiPicker(
    onEmojiSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val emojis = listOf(
        "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂",
        "🙂", "🙃", "😉", "😊", "😇", "🥰", "😍", "🤩",
        "😘", "😗", "😚", "😙", "🥲", "😋", "😛", "😜",
        "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔", "🤐",
        "🤨", "😐", "😑", "😶", "😏", "😒", "🙄", "😬",
        "😮‍💨", "🤥", "😌", "😔", "😪", "🤤", "😴", "😷",
        "👍", "👎", "👏", "🙌", "🤝", "🙏", "💪", "🤘",
        "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍",
        "💯", "💢", "💥", "💫", "💦", "💨", "🔥", "⭐",
        "🌟", "✨", "⚡", "💥", "🎉", "🎊", "🎈", "🎁",
        "😎", "🤓", "🧐", "🤨", "😕", "😟", "🙁", "☹️",
        "😮", "😯", "😲", "😳", "🥺", "😦", "😧", "😨"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = "表情",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            emojis.forEach { emoji ->
                EmojiItem(
                    emoji = emoji,
                    onClick = { onEmojiSelected(emoji) }
                )
            }
        }
    }
}

@Composable
fun EmojiItem(
    emoji: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}