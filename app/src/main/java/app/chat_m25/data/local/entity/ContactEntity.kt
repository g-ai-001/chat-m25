package app.chat_m25.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val avatar: String? = null,
    val remark: String = "",
    val phone: String = "",
    val isStarred: Boolean = false
)
