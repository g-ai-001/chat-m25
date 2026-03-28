package app.chat_m25.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ChatTypography,
        shapes = ChatShapes,
        content = content
    )
}
