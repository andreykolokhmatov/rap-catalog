package band.effective.education.rapcatalog.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF1DB954),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF0E5C2A),
    onPrimaryContainer = Color(0xFFB9F6CA),
    secondary = Color(0xFFB3B3B3),
    onSecondary = Color(0xFF000000),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF121212),
    onSurfaceVariant = Color(0xFFB3B3B3),
    outline = Color(0xFF3E3E3E),
    outlineVariant = Color(0xFF1F1F1F),
    error = Color(0xFFEF5350),
    onError = Color(0xFF000000),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF15803D),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFBBF7D0),
    onPrimaryContainer = Color(0xFF052E16),
    secondary = Color(0xFF4338CA),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F0F23),
    surface = Color(0xFFF8FAFC),
    onSurface = Color(0xFF0F0F23),
    surfaceVariant = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF4B5162),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color(0xFFFFFFFF),
)

@Composable
fun AppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
