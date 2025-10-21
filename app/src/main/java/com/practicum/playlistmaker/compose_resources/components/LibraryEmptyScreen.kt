package com.practicum.playlistmaker.compose_resources.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R

@Composable
fun LibraryEmptyScreen(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_nothing_found),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}