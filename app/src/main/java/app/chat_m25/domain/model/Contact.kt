package app.chat_m25.domain.model

data class Contact(
    val id: Long = 0,
    val name: String,
    val avatar: String? = null,
    val remark: String = "",
    val phone: String = "",
    val isStarred: Boolean = false
)
