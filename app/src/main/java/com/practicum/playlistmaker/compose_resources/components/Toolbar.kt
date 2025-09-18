package com.practicum.playlistmaker.compose_resources.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(title: String) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontSize = 22.sp
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.background),
            scrolledContainerColor = colorResource(R.color.background)
        ),
        windowInsets = WindowInsets(0)
    )
}