package com.practicum.playlistmaker.search.track_search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import com.practicum.playlistmaker.search.track_search.presentation.models.SearchScreenState
import com.practicum.playlistmaker.util.Utils

class SearchActivity : AppCompatActivity() {

    companion object {
        //private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val DEBOUNCE_DELAY = 2000L
        const val TRACK = "TRACK"
    }

    private lateinit var binding: ActivitySearchBinding
    private val viewModel by viewModels<SearchViewModel> { SearchViewModel.getViewModelFactory() }

    private var searchQuery: String? = null
    private val tracks = mutableListOf<Track>()
    private lateinit var trackListAdapter: SearchAdapter
    private lateinit var tracksHistoryAdapter: SearchAdapter
    private var tracksHistory = listOf<Track>()
    private var textWatcher: TextWatcher? = null

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
            tracksHistoryAdapter.updateTrackList(history)
        }

        setupSearchField()

        setupSearchResultContainer()

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is SearchScreenState.Loading -> {
                    binding.run {
                        progressBar.isVisible = true
                        trackList.isVisible = false
                        emptyScreen.isVisible = false
                    }
                }

                is SearchScreenState.Content -> {
                    trackListAdapter.updateTrackList(screenState.trackList)
                    binding.run {
                        progressBar.isVisible = false
                        trackList.isVisible = true
                        emptyScreen.isVisible = false
                    }
                }

                is SearchScreenState.Error -> {
                    showErrorScreen(screenState.errorType)
                    binding.run {
                        progressBar.isVisible = false
                        trackList.isVisible = false
                        emptyScreen.isVisible = true
                    }
                }
            }
        }

//old


        //searchField.setOnFocusChangeListener { _, hasFocus ->
        //    if (hasFocus && searchQuery.isNullOrEmpty()) {
        //        if (tracksHistory.isEmpty()) tracksHistory = searchHistory.getTracks()
        //        tracksHistoryAdapter.trackList = tracksHistory.asReversed()
        //        historyContainer.isVisible = tracksHistory.isNotEmpty()
        //    } else historyContainer.isVisible = false
        //}

        //findViewById<MaterialButton>(R.id.track_history_clear).setOnClickListener {
        //    searchHistory.clearHistory()
        //    tracksHistoryAdapter.trackList = listOf()
        //    tracksHistoryAdapter.notifyDataSetChanged()
        //    historyContainer.isVisible = false
        //}
    }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { loadData() }
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, DEBOUNCE_DELAY)
    }

    private var isClickAllowed = true
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, DEBOUNCE_DELAY)
        }
        return current
    }

    //override fun onSaveInstanceState(outState: Bundle) {
    //    super.onSaveInstanceState(outState)
    //    outState.putString(SEARCH_TEXT, searchQuery)
    //}
//
    //override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    //    super.onRestoreInstanceState(savedInstanceState)
    //    searchField.setText(savedInstanceState.getString(SEARCH_TEXT))
    //}

    //@SuppressLint("NotifyDataSetChanged")
    private fun loadData() {
        if (!searchQuery.isNullOrEmpty()) {
            viewModel.searchTrack(searchQuery!!)

            //val progressBar = findViewById<CircularProgressIndicator>(R.id.progressBar)
            //progressBar.isVisible = true
            //rwTrackList.isVisible = false
            //historyContainer.isVisible = false
            //emptyScreen.isVisible = false
//
            //Creator.provideSearchInteractor()
            //    .searchTrack(searchQuery ?: "", object : SearchInteractor.TrackConsumer {
            //        override fun consume(foundTracks: List<Track>?, errorType: ErrorType?) {
            //            handler.post {
            //                progressBar.isVisible = false
            //                when {
            //                    errorType != null -> toggleEmptyScreen(errorType)
//
            //                    foundTracks != null -> {
            //                        tracks.clear()
            //                        tracks.addAll(foundTracks)
            //                        trackListAdapter.notifyDataSetChanged()
            //                        rwTrackList.isVisible = true
            //                        rwTrackList.layoutManager?.smoothScrollToPosition(
            //                            rwTrackList, null, 0
            //                        )
            //                        toggleEmptyScreen(null)
            //                    }
            //                }
            //            }
            //        }
            //    })
        }
    }

    //private fun toggleEmptyScreen(type: ErrorType?) {
    //    val shouldShow = type != null
//
    //    emptyScreen.isVisible = shouldShow
    //    rwTrackList.isVisible = !shouldShow
//
    //    if (shouldShow) {
    //        findViewById<ImageView>(R.id.empty_screen_image).setImageResource(type!!.imageId)
    //        findViewById<TextView>(R.id.empty_screen_text).setText(type.messageId)
//
    //        val buttonRetry = findViewById<MaterialButton>(R.id.empty_screen_button)
    //        buttonRetry.isVisible = type == ErrorType.NoInternet
    //        buttonRetry.setOnClickListener {
    //            loadData()
    //        }
    //    }
    //}

    private fun setupHistory() {
        binding.run {
            trackHistoryList.layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            tracksHistoryAdapter = SearchAdapter { track ->
                if (clickDebounce()) startActivity(
                    Intent(this@SearchActivity, PlayerActivity::class.java).putExtra(
                        TRACK,
                        Utils().serializeToJson(track)
                    )
                )
            }
            trackHistoryList.adapter = tracksHistoryAdapter

            trackHistoryClear.setOnClickListener {
                viewModel.clearHistory()
            }
        }
    }

    private fun setupSearchField() {
        binding.run {
            searchClear.setOnClickListener {
                searchInput.setText("")
                val inputManager =
                    this@SearchActivity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(searchInput.windowToken, 0)
                searchInput.clearFocus()
            }

            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    //empty
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    searchClear.isVisible = !searchInput.text.isNullOrEmpty()
                    searchDebounce()
                }

                override fun afterTextChanged(p0: Editable?) {
                    //searchQuery = searchInput.text.toString()
//
                    //if (searchQuery.isNullOrEmpty()) {
                    //    rwTrackList.isVisible = false
                    //    emptyScreen.isVisible = false
                    //}
//
                    //historyContainer.isVisible =
                    //    searchInput.hasFocus() && searchQuery.isNullOrEmpty() && tracksHistoryAdapter.trackList.isNotEmpty()
                }
            }
            searchInput.addTextChangedListener(textWatcher)

            searchInput.setOnFocusChangeListener { _, hasFocus ->
                changeHistoryVisibility(hasFocus && searchInput.text.isNullOrEmpty())
            }
        }
    }

    private fun setupSearchResultContainer() {
        binding.run {
            trackList.layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            trackListAdapter = SearchAdapter { track ->
                if (clickDebounce()) {
                    //tracksHistory = searchHistory.addTrack(track)
                    //tracksHistoryAdapter.trackList = tracksHistory
                    //tracksHistoryAdapter.notifyDataSetChanged()
                    viewModel.addToHistory(track)
                    startActivity(
                        Intent(this@SearchActivity, PlayerActivity::class.java).putExtra(
                            TRACK,
                            Utils().serializeToJson(track)
                        )
                    )
                }
            }
            //trackListAdapter.trackList = tracks
            trackList.adapter = trackListAdapter
        }
    }

    private fun changeHistoryVisibility(shouldShow: Boolean) {
        binding.historyContainer.isVisible =
            (shouldShow && !viewModel.getHistoryLiveData().value.isNullOrEmpty())
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