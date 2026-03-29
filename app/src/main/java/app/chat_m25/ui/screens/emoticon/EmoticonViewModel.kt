package app.chat_m25.ui.screens.emoticon

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class EmoticonCategory(
    val name: String,
    val emojis: List<String>
)

data class EmoticonUiState(
    val categories: List<EmoticonCategory> = emptyList(),
    val selectedCategoryIndex: Int = 0
)

@HiltViewModel
class EmoticonViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EmoticonUiState())
    val uiState: StateFlow<EmoticonUiState> = _uiState.asStateFlow()

    init {
        loadEmoticons()
    }

    private fun loadEmoticons() {
        val categories = listOf(
            EmoticonCategory(
                name = "笑脸",
                emojis = listOf(
                    "😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂",
                    "🙂", "🙃", "😉", "😊", "😇", "🥰", "😍", "🤩",
                    "😘", "😗", "😚", "😙", "🥲", "😋", "😛", "😜",
                    "🤪", "😝", "🤑", "🤗", "🤭", "🤫", "🤔", "🤐",
                    "😮‍💨", "🤥", "😌", "😔", "😪", "🤤", "😴", "😷"
                )
            ),
            EmoticonCategory(
                name = "手势",
                emojis = listOf(
                    "👍", "👎", "👏", "🙌", "🤝", "🙏", "💪", "🤘",
                    "👌", "🤌", "🤏", "✌️", "🤞", "🖖", "🤙", "💋",
                    "🫶", "🫀", "🦾", "🦿", "🦵", "🦶", "👂", "🦻"
                )
            ),
            EmoticonCategory(
                name = "爱心",
                emojis = listOf(
                    "❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍",
                    "🤎", "💕", "💞", "💓", "💗", "💖", "💘", "💝",
                    "💟", "♥️", "❣️", "💔", "❤️‍🔥", "❤️‍🩹"
                )
            ),
            EmoticonCategory(
                name = "符号",
                emojis = listOf(
                    "💯", "💢", "💥", "💫", "💦", "💨", "🔥", "⭐",
                    "🌟", "✨", "⚡", "💥", "🎉", "🎊", "🎈", "🎁",
                    "🏆", "🥇", "🥈", "🥉", "🏅", "🎖️", "🎗️", "🎪"
                )
            ),
            EmoticonCategory(
                name = "自然",
                emojis = listOf(
                    "🌸", "🌺", "🌹", "🌷", "🌱", "🌿", "☘️", "🍀",
                    "🍁", "🍃", "🌲", "🌳", "🌴", "🌵", "🌾", "🌽",
                    "🌻", "🌼", "🪴", "🪷", "🪹", "🪺", "🦀", "🦞"
                )
            ),
            EmoticonCategory(
                name = "食物",
                emojis = listOf(
                    "🍎", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐",
                    "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅",
                    "🍕", "🍔", "🍟", "🌭", "🍿", "🍩", "🍪", "🎂",
                    "🍰", "🧁", "🍫", "🍬", "🍭", "🍮", "🍵", "☕"
                )
            ),
            EmoticonCategory(
                name = "物品",
                emojis = listOf(
                    "⌚", "📱", "💻", "⌨️", "🖥️", "🖨️", "🖱️", "🖲️",
                    "💽", "💾", "💿", "📀", "📼", "📷", "📸", "📹",
                    "🎥", "📞", "☎️", "📟", "📠", "📺", "📻", "🎙️",
                    "🎚️", "🎛️", "🧭", "⏱️", "⏲️", "⏰", "🕰️", "⌛"
                )
            ),
            EmoticonCategory(
                name = "动物",
                emojis = listOf(
                    "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼",
                    "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🐔",
                    "🐧", "🐦", "🐤", "🦆", "🦅", "🦉", "🦇", "🐺",
                    "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞"
                )
            )
        )

        _uiState.value = _uiState.value.copy(categories = categories)
    }

    fun selectCategory(index: Int) {
        _uiState.value = _uiState.value.copy(selectedCategoryIndex = index)
    }
}