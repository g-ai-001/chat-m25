package app.chat_m25.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")

enum class ThemeMode(val value: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromValue(value: Int): ThemeMode = entries.firstOrNull { it.value == value } ?: SYSTEM
    }
}

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeModeKey = intPreferencesKey("theme_mode")

    val themeMode: Flow<ThemeMode> = context.themeDataStore.data.map { preferences ->
        ThemeMode.fromValue(preferences[themeModeKey] ?: ThemeMode.SYSTEM.value)
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[themeModeKey] = mode.value
        }
    }
}