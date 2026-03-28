package app.chat_m25.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import app.chat_m25.data.repository.ThemeMode
import app.chat_m25.data.repository.ThemePreferences

private val LightColorScheme = lightColorScheme(
    primary = WeChatGreen,
    onPrimary = ChatTextOnPrimary,
    primaryContainer = WeChatGreenLight,
    onPrimaryContainer = ChatTextPrimary,
    secondary = WeChatGreenDark,
    onSecondary = ChatTextOnPrimary,
    background = ChatBackground,
    onBackground = ChatTextPrimary,
    surface = ChatSurface,
    onSurface = ChatTextPrimary,
    surfaceVariant = ChatBackground,
    onSurfaceVariant = ChatTextSecondary,
    outline = ChatDivider,
    error = ChatRed,
    onError = ChatTextOnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = WeChatGreen,
    onPrimary = ChatTextOnPrimary,
    primaryContainer = WeChatGreenDark,
    onPrimaryContainer = ChatTextOnPrimary,
    secondary = WeChatGreenLight,
    onSecondary = ChatTextOnPrimary,
    background = ChatBackgroundDark,
    onBackground = ChatTextOnPrimary,
    surface = ChatSurfaceDarkTheme,
    onSurface = ChatTextOnPrimary,
    surfaceVariant = ChatSurfaceDark,
    onSurfaceVariant = ChatTextSecondary,
    outline = ChatDivider,
    error = ChatRed,
    onError = ChatTextOnPrimary
)

@Composable
fun ChatTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val themePreferences = ThemePreferences(context)
    val themeMode by themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)

    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ChatTypography,
        shapes = ChatShapes,
        content = content
    )
}