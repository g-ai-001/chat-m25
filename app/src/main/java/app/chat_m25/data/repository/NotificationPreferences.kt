package app.chat_m25.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(name = "notification_settings")

enum class NotificationSound(val value: String) {
    DEFAULT("默认"),
    SIREN("警笛"),
    CHIME("风铃"),
    POP("气泡"),
    SILENT("静音");

    companionion object {
        fun fromValue(value: String): NotificationSound = entries.firstOrNull { it.value == value } ?: DEFAULT
    }
}

data class NotificationSettings(
    val enabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val sound: NotificationSound = NotificationSound.DEFAULT
)

@Singleton
class NotificationPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationEnabledKey = booleanPreferencesKey("notification_enabled")
    private val soundEnabledKey = booleanPreferencesKey("sound_enabled")
    private val vibrationEnabledKey = booleanPreferencesKey("vibration_enabled")
    private val soundKey = stringPreferencesKey("notification_sound")

    val notificationSettings: Flow<NotificationSettings> = context.notificationDataStore.data.map { preferences ->
        NotificationSettings(
            enabled = preferences[notificationEnabledKey] ?: true,
            soundEnabled = preferences[soundEnabledKey] ?: true,
            vibrationEnabled = preferences[vibrationEnabledKey] ?: true,
            sound = NotificationSound.fromValue(preferences[soundKey] ?: NotificationSound.DEFAULT.value)
        )
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { preferences ->
            preferences[notificationEnabledKey] = enabled
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { preferences ->
            preferences[soundEnabledKey] = enabled
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.notificationDataStore.edit { preferences ->
            preferences[vibrationEnabledKey] = enabled
        }
    }

    suspend fun setSound(sound: NotificationSound) {
        context.notificationDataStore.edit { preferences ->
            preferences[soundKey] = sound.value
        }
    }
}
