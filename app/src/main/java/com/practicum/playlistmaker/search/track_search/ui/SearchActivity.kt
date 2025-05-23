package com.practicum.playlistmaker.search.track_search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import com.practicum.playlistmaker.search.track_search.presentation.models.SearchScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        const val TRACK = "TRACK"
    }

    private lateinit var binding: ActivitySearchBinding
    private val viewModel by viewModel<SearchViewModel>()

    private var searchQuery: String? = null
    private var textWatcher: TextWatcher? = null
    private lateinit var trackListAdapter: SearchAdapter
    private lateinit var tracksHistoryAdapter: SearchAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toolbarSearch.setNavigationOnClickListener {
            this.finish()
        }

        setupHistory()

        viewModel.getHistoryLiveData().observe(this) { history ->
            tracksHistoryAdapter.updateTrackList(history.asReversed())
        }

        setupSearchField()

        setupSearchResultContainer()

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is SearchScreenState.Initial -> setupInitialScreen()

                is SearchScreenState.Focused -> {
                    changeHistoryVisibility(screenState.isHistoryAvailable)
                    binding.searchClearButton.isVisible = false
                }

                is SearchScreenState.Typing -> {
                    binding.run {
                        changeHistoryVisibility(false)
                        searchClearButton.isVisible = true
                        emptyScreen.isVisible = false
                    }
                }

                is SearchScreenState.Loading -> changeContentVisibility(
                    progressBarVisibility = true,
                    trackListVisibility = false,
                    emptyScreenVisibility = false
                )

                is SearchScreenState.Content -> {
                    trackListAdapter.updateTrackList(screenState.trackList)
                    changeContentVisibility(
                        progressBarVisibility = false,
                        trackListVisibility = true,
                        emptyScreenVisibility = false
                    )
                }

                is SearchScreenState.Error -> {
                    showErrorScreen(screenState.errorType)
                    changeContentVisibility(
                        progressBarVisibility = false,
                        trackListVisibility = false,
                        emptyScreenVisibility = true
                    )
                }

                is SearchScreenState.OpenPlayer -> startActivity(
                    Intent(this@SearchActivity, PlayerActivity::class.java).putExtra(
                        TRACK,
                        screenState.serializedTrack
                    )
                )
            }
        }
    }

    private fun setupHistory() {
        binding.run {
            trackHistoryList.layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            tracksHistoryAdapter = SearchAdapter { track ->
                viewModel.performTrackClick(track)
            }
            trackHistoryList.adapter = tracksHistoryAdapter

            trackHistoryClear.setOnClickListener {
                viewModel.clearHistory()
            }
        }
    }

    private fun setupSearchField() {
        binding.run {
            searchClearButton.setOnClickListener {
                viewModel.setInitialState()
            }

            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    //empty
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (searchInput.text.isNullOrEmpty()) viewModel.setFocusedState()
                    else viewModel.setTypingState()
                }

                override fun afterTextChanged(p0: Editable?) {
                    searchQuery = searchInput.text.toString()
                    loadData()
                }
            }
            searchInput.addTextChangedListener(textWatcher)

            searchInput.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) viewModel.setFocusedState()
            }
        }
    }

    private fun setupInitialScreen() {
        binding.run {
            searchInput.setText("")
            val inputManager =
                this@SearchActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(searchInput.windowToken, 0)
            searchInput.clearFocus()

            progressBar.isVisible = false
            historyContainer.isVisible = false
            searchClearButton.isVisible = false
            trackList.isVisible = false
            emptyScreen.isVisible = false
        }
    }

    private fun setupSearchResultContainer() {
        binding.run {
            trackList.layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            trackListAdapter = SearchAdapter { track ->
                viewModel.addToHistory(track)
                viewModel.performTrackClick(track)
            }
            trackList.adapter = trackListAdapter
        }
    }

    private fun changeHistoryVisibility(shouldShow: Boolean) {
        binding.run {
            historyContainer.isVisible = shouldShow
            trackList.isVisible = false
        }
    }

    private fun changeContentVisibility(
        progressBarVisibility: Boolean,
        trackListVisibility: Boolean,
        emptyScreenVisibility: Boolean
    ) {
        binding.run {
            progressBar.isVisible = progressBarVisibility
            trackList.isVisible = trackListVisibility
            emptyScreen.isVisible = emptyScreenVisibility
        }
    }

    private fun loadData() {
        if (!searchQuery.isNullOrEmpty()) {
            viewModel.searchWithDebounce(searchQuery!!)
        }
    }

    private fun showErrorScreen(error: ErrorType) {
        binding.run {
            emptyScreenImage.setImageResource(error.imageId)
            emptyScreenText.setText(error.messageId)

            emptyScreenButton.isVisible = error == ErrorType.NoInternet
            emptyScreenButton.setOnClickListener {
                loadData()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.searchInput.removeTextChangedListener(textWatcher)
    }
}