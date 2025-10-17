package com.practicum.playlistmaker.search.track_search.ui

import SearchText
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.compose_resources.components.Toolbar
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import com.practicum.playlistmaker.search.track_search.presentation.models.SearchScreenState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel()) {

    val screenState by viewModel.getScreenStateFlow().collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Toolbar(stringResource(R.string.search))
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = MaterialTheme.colorScheme.secondary)
            //.background(colorResource(R.color.background))
        ) {
            SearchText(
                onFocusChanged = { focused -> if (focused) viewModel.setFocusedState() },
                onTextChanged = { text ->
                    if (text.isEmpty()) viewModel.setFocusedState()
                    else viewModel.searchWithDebounce(text)
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.error)
            ) {
                when (screenState) {
                    is SearchScreenState.Initial -> {
                        //TODO()
                    }

                    is SearchScreenState.Loading -> {
                        //TODO()
                    }

                    is SearchScreenState.Content -> {
                        //TODO()
                    }

                    is SearchScreenState.Focused -> if ((screenState as SearchScreenState.Focused).isHistoryAvailable) Toast.makeText(
                        context,
                        (screenState as SearchScreenState.Focused).isHistoryAvailable.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    is SearchScreenState.Error -> {
                        //TODO()
                    }

                    is SearchScreenState.Typing -> {
                        //TODO()
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}