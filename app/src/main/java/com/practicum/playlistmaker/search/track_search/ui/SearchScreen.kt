package com.practicum.playlistmaker.search.track_search.ui

import SearchField
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.compose_resources.components.ProgressBar
import com.practicum.playlistmaker.compose_resources.components.RoundedButton
import com.practicum.playlistmaker.compose_resources.components.Toolbar
import com.practicum.playlistmaker.compose_resources.components.TrackList
import com.practicum.playlistmaker.compose_resources.components.TracksHistory
import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import com.practicum.playlistmaker.search.track_search.presentation.models.SearchScreenState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel()) {

    val screenState by viewModel.getScreenStateFlow().collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Toolbar(stringResource(R.string.search))
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            SearchField(
                onFocusChanged = { focused -> if (focused) viewModel.setFocused() else viewModel.setInitialState() },
                onTextChanged = { text ->
                    if (text.isEmpty()) viewModel.setFocused()
                    else {
                        searchQuery = text
                        viewModel.searchWithDebounce(searchQuery)
                    }
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.primary)
            ) {
                when (screenState) {
                    is SearchScreenState.Initial -> {}

                    is SearchScreenState.Loading -> {
                        ProgressBar()
                    }

                    is SearchScreenState.Content -> {
                        val stateContent = screenState as SearchScreenState.Content
                        if (stateContent.isHistory) {
                            TracksHistory(
                                tracks = stateContent.trackList,
                                trackClickListener = viewModel::openTrackWithDebounce,
                                buttonClickListener = viewModel::clearHistory
                            )
                        } else {
                            TrackList(
                                tracks = stateContent.trackList,
                                clickListener = { track ->
                                    viewModel.addToHistory(track)
                                    viewModel.openTrackWithDebounce(track)
                                }
                            )
                        }
                    }

                    is SearchScreenState.Error -> {
                        val stateError = screenState as SearchScreenState.Error
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(86.dp))
                            Image(
                                painter = painterResource(stateError.errorType.imageId),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(stateError.errorType.messageId),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            if (stateError.errorType == ErrorType.NoInternet) {
                                Spacer(modifier = Modifier.height(24.dp))
                                RoundedButton(
                                    text = stringResource(R.string.search_retry),
                                    clickListener = { viewModel.searchWithDebounce(searchQuery) }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}