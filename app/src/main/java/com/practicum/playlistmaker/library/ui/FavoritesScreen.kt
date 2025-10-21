package com.practicum.playlistmaker.library.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.compose_resources.components.ProgressBar
import com.practicum.playlistmaker.compose_resources.components.TrackList
import com.practicum.playlistmaker.library.presentation.FavoritesViewModel
import com.practicum.playlistmaker.library.presentation.models.FavoritesScreenState
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    trackViewModel: SearchViewModel = koinViewModel()
) {
    val screenState by viewModel.getScreenStateFlow().collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        when (screenState) {
            is FavoritesScreenState.Loading -> {
                ProgressBar()
            }

            is FavoritesScreenState.Content -> {
                TrackList(
                    tracks = (screenState as FavoritesScreenState.Content).favoriteTracks,
                    clickListener = { track ->
                        trackViewModel.openTrackWithDebounce(track.copy(isFavorite = true))
                    }
                )
            }

            is FavoritesScreenState.Empty -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(86.dp))
                    Image(
                        painter = painterResource(R.drawable.ic_nothing_found),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.library_empty),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}