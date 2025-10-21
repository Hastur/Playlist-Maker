package com.practicum.playlistmaker.library.ui

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.compose_resources.components.Toolbar
import kotlinx.coroutines.launch

@Composable

fun LibraryScreen(fragmentManager: FragmentManager) {
    val tabs = listOf(stringResource(R.string.favorites), stringResource(R.string.playlists))
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { Toolbar(stringResource(R.string.library)) },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = MaterialTheme.colorScheme.primary)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.padding(horizontal = 16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        height = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    )
                }
            }
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> {
                        val fragmentId = remember { View.generateViewId() }

                        AndroidView(
                            modifier = Modifier
                                .fillMaxSize(),
                            factory = { context ->
                                FragmentContainerView(context).apply {
                                    id = fragmentId
                                }
                            },
                            update = {
                                if (fragmentManager.findFragmentById(fragmentId) == null) {
                                    fragmentManager.commit {
                                        replace(fragmentId, FavoritesFragment())
                                    }
                                }
                            }
                        )
                    }

                    1 -> {
                        val fragmentId = remember { View.generateViewId() }

                        AndroidView(
                            modifier = Modifier
                                .fillMaxSize(),
                            factory = { context ->
                                FragmentContainerView(context).apply {
                                    id = fragmentId
                                }
                            },
                            update = {
                                if (fragmentManager.findFragmentById(fragmentId) == null) {
                                    fragmentManager.commit {
                                        replace(fragmentId, PlaylistsFragment())
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}