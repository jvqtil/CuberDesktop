package dev.jvqtil.cuber.desktop.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF8A3D),
    onPrimary = Color(0xFF2A1307),
    primaryContainer = Color(0xFF512812),
    onPrimaryContainer = Color(0xFFFFDBC8),
    secondary = Color(0xFF9A9A9A),
    onSecondary = Color(0xFF1B1B1B),
    secondaryContainer = Color(0xFF303030),
    onSecondaryContainer = Color(0xFFE3E3E3),
    tertiary = Color(0xFF8C8C8C),
    onTertiary = Color(0xFF1B1B1B),
    tertiaryContainer = Color(0xFF303030),
    onTertiaryContainer = Color(0xFFE3E3E3),
    background = Color(0xFF141414),
    onBackground = Color(0xFFE9E9E9),
    surface = Color(0xFF191919),
    onSurface = Color(0xFFE9E9E9),
    surfaceVariant = Color(0xFF242424),
    onSurfaceVariant = Color(0xFFB5B5B5),
    surfaceContainerLowest = Color(0xFF101010),
    surfaceContainerLow = Color(0xFF181818),
    surfaceContainer = Color(0xFF1E1E1E),
    surfaceContainerHigh = Color(0xFF262626),
    surfaceContainerHighest = Color(0xFF2D2D2D),
    outline = Color(0xFF3B3B3B),
    outlineVariant = Color(0xFF2E2E2E),
    error = Color(0xFFFF6B6B),
    onError = Color(0xFF2A0808),
    errorContainer = Color(0xFF4A1818),
    onErrorContainer = Color(0xFFFFDAD6),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFFB85F12),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDCC4),
    onPrimaryContainer = Color(0xFF3F1D05),
    secondary = Color(0xFF656565),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE3E3E3),
    onSecondaryContainer = Color(0xFF202020),
    tertiary = Color(0xFF777777),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE5E5E5),
    onTertiaryContainer = Color(0xFF202020),
    background = Color(0xFFF3F3F3),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE7E7E7),
    onSurfaceVariant = Color(0xFF5E5E5E),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F7F7),
    surfaceContainer = Color(0xFFF0F0F0),
    surfaceContainerHigh = Color(0xFFE9E9E9),
    surfaceContainerHighest = Color(0xFFE0E0E0),
    outline = Color(0xFF767676),
    outlineVariant = Color(0xFFD0D0D0),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val DesktopShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(14.dp),
)

@Composable
fun DesktopTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) {
            DarkColors
        } else {
            LightColors
        },
        shapes = DesktopShapes,
        content = content,
    )
}
