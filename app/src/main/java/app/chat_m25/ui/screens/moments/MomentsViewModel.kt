package app.chat_m25.ui.screens.moments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.chat_m25.data.repository.MomentRepository
import app.chat_m25.domain.model.Moment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MomentsUiState(
    val moments: List<Moment> = emptyList(),
    val isLoading: Boolean = false,
    val isPublishing: Boolean = false,
    val showPublishDialog: Boolean = false,
    val publishContent: String = "",
    val publishImages: List<String> = emptyList()
)

@HiltViewModel
class MomentsViewModel @Inject constructor(
    private val momentRepository: MomentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MomentsUiState())
    val uiState: StateFlow<MomentsUiState> = _uiState.asStateFlow()

    init {
        loadMoments()
        createDemoMoments()
    }

    private fun loadMoments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            momentRepository.getAllMoments().collect { moments ->
                _uiState.update { it.copy(moments = moments, isLoading = false) }
            }
        }
    }

    fun showPublishDialog() {
        _uiState.update { it.copy(showPublishDialog = true) }
    }

    fun hidePublishDialog() {
        _uiState.update { it.copy(showPublishDialog = false, publishContent = "", publishImages = emptyList()) }
    }

    fun updatePublishContent(content: String) {
        _uiState.update { it.copy(publishContent = content) }
    }

    fun updatePublishImages(images: List<String>) {
        _uiState.update { it.copy(publishImages = images) }
    }

    fun publish() {
        val state = _uiState.value
        if (state.publishContent.isBlank() && state.publishImages.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPublishing = true) }
            momentRepository.createMoment(
                userId = 1,
                userName = "我",
                content = state.publishContent,
                images = state.publishImages
            )
            _uiState.update { it.copy(isPublishing = false, showPublishDialog = false, publishContent = "", publishImages = emptyList()) }
        }
    }

    fun toggleLike(momentId: Long) {
        viewModelScope.launch {
            momentRepository.toggleLike(momentId)
        }
    }

    fun deleteMoment(momentId: Long) {
        viewModelScope.launch {
            momentRepository.deleteMoment(momentId)
        }
    }

    private fun createDemoMoments() {
        if (_uiState.value.moments.isEmpty()) {
            viewModelScope.launch {
                val demoMoments = listOf(
                    Triple(1L, "张三", "今天天气真好，适合出去走走！"),
                    Triple(2L, "李四", "分享一张照片"),
                    Triple(3L, "王五", "新买了一部手机，很不错！")
                )
                demoMoments.forEach { (userId, userName, content) ->
                    momentRepository.createMoment(userId, userName, content, emptyList())
                }
            }
        }
    }

    fun addDemoMessages() {
        viewModelScope.launch {
            val messages = listOf(
                Triple(4L, "赵六", "收到消息了吗？"),
                Triple(5L, "钱七", "周末有空吗？"),
                Triple(6L, "孙八", "一起吃饭吧！")
            )
            messages.forEach { (userId, userName, content) ->
                momentRepository.createMoment(userId, userName, content, emptyList())
            }
        }
    }
}