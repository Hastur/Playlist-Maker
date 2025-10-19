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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PlaylistMakerTypography,
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
    primary = Color(0xFF673AB7),
    //onPrimary = Color.White,
    secondary = Color(0xFF9C27B0),
    //onSecondary = Color.White,
    background = white,
    //onBackground = Color.Black,
    surface = Color.White,
    //onSurface = Color.Black,
    surfaceVariant = lightGrey,
    onSurfaceVariant = grey,
    error = Color(0xFFB00020)
    //onError = Color.White,
)
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    //onPrimary = Color.Black,
    secondary = Color(0xFF03DAC6),
    //onSecondary = Color.Black,
    background = darkestGrey,
    //onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    //onSurface = Color.White,
    surfaceVariant = white,
    onSurfaceVariant = darkestGrey,
    error = Color(0xFFCF6679)
    //onError = Color.Black
)

val PlaylistMakerTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontSize = 22.sp
    )
    /*bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),*/
)