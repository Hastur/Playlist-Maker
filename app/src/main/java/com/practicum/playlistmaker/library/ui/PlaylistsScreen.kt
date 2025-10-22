package com.practicum.playlistmaker.library.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.compose_resources.components.LibraryEmptyScreen
import com.practicum.playlistmaker.compose_resources.components.PlaylistsGrid
import com.practicum.playlistmaker.compose_resources.components.ProgressBar
import com.practicum.playlistmaker.compose_resources.components.RoundedButton
import com.practicum.playlistmaker.library.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.library.presentation.models.PlaylistsScreenState
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistsScreen(viewModel: PlaylistsViewModel = koinViewModel()) {
    val screenState by viewModel.getScreenStateFlow().collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .padding(top = 16.dp)
            .background(color = MaterialTheme.colorScheme.primary)
    ) {
        RoundedButton(
            text = stringResource(R.string.new_playlist),
            clickListener = { viewModel.onButtonAddClicked() }
        )
        when (screenState) {
            is PlaylistsScreenState.Loading -> {
                ProgressBar()
            }

            is PlaylistsScreenState.Content -> {
                PlaylistsGrid(
                    playlists = (screenState as PlaylistsScreenState.Content).playlists,
                    clickListener = { playlist ->
                        viewModel.onPlaylistClicked(playlist)
                    }
                )
            }

            is PlaylistsScreenState.Empty -> {
                LibraryEmptyScreen(stringResource(R.string.no_playlists))
            }
        }
    }
}