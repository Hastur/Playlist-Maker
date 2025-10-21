package com.practicum.playlistmaker.compose_resources.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.models.Playlist

@Composable
fun PlaylistItem(playlist: Playlist, clickListener: (Playlist) -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .padding(top = 16.dp)
            .clickable { clickListener(playlist) }) {
        AsyncImage(
            model = playlist.coverPath,
            contentDescription = playlist.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            placeholder = painterResource(R.drawable.ic_track_placeholder),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.ic_track_placeholder)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = playlist.name,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        val quantityPlural = pluralStringResource(
            R.plurals.numberOfTracks,
            playlist.tracksIds.size,
            playlist.tracksIds.size
        )
        Text(
            text = quantityPlural,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}