package com.practicum.playlistmaker.search.track_search.ui

import SearchText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.compose_resources.components.Toolbar
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel()) {
    Scaffold(
        topBar = {
            Toolbar(stringResource(R.string.search))
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(colorResource(R.color.background))
        ) {
            SearchText()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}