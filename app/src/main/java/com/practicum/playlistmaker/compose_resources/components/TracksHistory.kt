package com.practicum.playlistmaker.compose_resources.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.track_search.domain.models.Track

@Composable
fun TracksHistory(
    tracks: List<Track>,
    trackClickListener: (Track) -> Unit,
    buttonClickListener: () -> Unit
) {
    LazyColumn {
        item {
            Text(
                text = stringResource(R.string.search_history),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 34.dp)
                    .padding(bottom = 20.dp)
            )
        }
        items(tracks) { track ->
            TrackItem(track, trackClickListener)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
            RoundedButton(
                text = stringResource(R.string.clear_history),
                clickListener = buttonClickListener
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}