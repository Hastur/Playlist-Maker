package com.practicum.playlistmaker.compose_resources

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val playlistMakerTypography = Typography(
        titleLarge = TextStyle(
            color = colorScheme.onPrimary,
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium))
        ),
        titleMedium = TextStyle(
            color = colorScheme.onPrimary,
            fontSize = 19.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium))
        ),
        bodyMedium = TextStyle(
            color = black,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular))
        ),
        bodySmall = TextStyle(
            color = colorScheme.primary,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_medium))
        ),
        labelMedium = TextStyle(
            color = colorScheme.onPrimary,
            fontSize = 16.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular))
        ),
        labelSmall = TextStyle(
            color = colorScheme.onSecondary,
            fontSize = 11.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular))
        )
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = playlistMakerTypography,
        content = content
    )
}

val white = Color(0xFFFFFFFF)
val black = Color(0xFF000000)
val blue = Color(0xFF3772E7)
val darkestGrey = Color(0xFF1A1B22)
val grey = Color(0xFFAEAFB4)
val lightGrey = Color(0xFFE6E8EB)

val LightColorScheme = lightColorScheme(
    primary = white,
    onPrimary = darkestGrey,
    onSecondary = grey,
    tertiary = blue,
    surfaceVariant = lightGrey,
    onSurfaceVariant = grey
)
val DarkColorScheme = darkColorScheme(
    primary = darkestGrey,
    onPrimary = white,
    onSecondary = white,
    tertiary = blue,
    surfaceVariant = white,
    onSurfaceVariant = darkestGrey
)