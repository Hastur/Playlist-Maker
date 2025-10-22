package com.practicum.playlistmaker.compose_resources.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomSwitch(
    isChecked: Boolean
) {
    val trackWidth = 32.dp
    val trackHeight = 14.dp
    val thumbDiameter = 20.dp

    Box(
        modifier = Modifier.size(width = trackWidth, height = thumbDiameter)
    ) {
        Box(
            modifier = Modifier
                .size(width = trackWidth, height = trackHeight)
                .align(Alignment.Center)
                .background(
                    color = if (isChecked) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.scrim,
                    shape = RoundedCornerShape(percent = 50)
                )
        )
        Box(
            modifier = Modifier
                .size(thumbDiameter)
                .align(Alignment.CenterStart)
                .offset(
                    x = if (isChecked) (trackWidth - thumbDiameter) else 0.dp
                )
                .background(
                    color = if (isChecked) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSecondary,
                    shape = CircleShape
                )
        )
    }
}