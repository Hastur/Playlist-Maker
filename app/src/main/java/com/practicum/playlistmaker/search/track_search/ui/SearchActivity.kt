package com.practicum.playlistmaker.search.track_search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.track_search.data.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.util.Utils

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val DEBOUNCE_DELAY = 2000L
        const val TRACK = "TRACK"
    }

    private lateinit var searchField: EditText
    private lateinit var clearButton: ImageView
    private lateinit var rwTrackList: RecyclerView
    private lateinit var historyContainer: ScrollView
    private lateinit var emptyScreen: LinearLayout
    private var searchQuery: String? = null
    private val tracks = mutableListOf<Track>()
    private lateinit var trackListAdapter: SearchAdapter
    private lateinit var tracksHistoryAdapter: SearchAdapter
    private var tracksHistory = listOf<Track>()
    private var textWatcher: TextWatcher? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Toolbar>(R.id.toolbar_search).setNavigationOnClickListener {
            this.finish()
        }

        searchField = findViewById(R.id.search_input)
        clearButton = findViewById(R.id.search_clear)
        historyContainer = findViewById(R.id.history_container)
        emptyScreen = findViewById(R.id.empty_screen)

        clearButton.setOnClickListener {
            searchField.setText("")
            val inputManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(searchField.windowToken, 0)
            searchField.clearFocus()
        }

        val searchHistory = Creator.provideSearchHistoryInteractor()
        val rwTracksHistory = findViewById<RecyclerView>(R.id.track_history_list)
        rwTracksHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        tracksHistoryAdapter = SearchAdapter { track ->
            if (clickDebounce()) startActivity(
                Intent(this, PlayerActivity::class.java).putExtra(
                    TRACK,
                    Utils().serializeToJson(track)
                )
            )
        }
        rwTracksHistory.adapter = tracksHistoryAdapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //empty
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.isVisible = !searchField.text.isNullOrEmpty()
                searchDebounce()
            }

            override fun afterTextChanged(p0: Editable?) {
                searchQuery = searchField.text.toString()

                if (searchQuery.isNullOrEmpty()) {
                    rwTrackList.isVisible = false
                    emptyScreen.isVisible = false
                }

                historyContainer.isVisible =
                    searchField.hasFocus() && searchQuery.isNullOrEmpty() && tracksHistoryAdapter.trackList.isNotEmpty()
            }
        }
        searchField.addTextChangedListener(textWatcher)

        rwTrackList = findViewById(R.id.track_list)
        rwTrackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackListAdapter = SearchAdapter { track ->
            if (clickDebounce()) {
                tracksHistory = searchHistory.addTrack(track)
                tracksHistoryAdapter.trackList = tracksHistory
                tracksHistoryAdapter.notifyDataSetChanged()

                startActivity(
                    Intent(this, PlayerActivity::class.java).putExtra(
                        TRACK,
                        Utils().serializeToJson(track)
                    )
                )
            }
        }
        trackListAdapter.trackList = tracks
        rwTrackList.adapter = trackListAdapter

        searchField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchQuery.isNullOrEmpty()) {
                if (tracksHistory.isEmpty()) tracksHistory = searchHistory.getTracks()
                tracksHistoryAdapter.trackList = tracksHistory.asReversed()
                historyContainer.isVisible = tracksHistory.isNotEmpty()
            } else historyContainer.isVisible = false
        }

        findViewById<MaterialButton>(R.id.track_history_clear).setOnClickListener {
            searchHistory.clearHistory()
            tracksHistoryAdapter.trackList = listOf()
            tracksHistoryAdapter.notifyDataSetChanged()
            historyContainer.isVisible = false
        }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchQuery)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchField.setText(savedInstanceState.getString(SEARCH_TEXT))
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadData() {
        if (!searchQuery.isNullOrEmpty()) {

            val progressBar = findViewById<CircularProgressIndicator>(R.id.progressBar)
            progressBar.isVisible = true
            rwTrackList.isVisible = false
            historyContainer.isVisible = false
            emptyScreen.isVisible = false

            Creator.provideSearchInteractor()
                .searchTrack(searchQuery ?: "", object : SearchInteractor.TrackConsumer {
                    override fun consume(foundTracks: List<Track>?, errorType: ErrorType?) {
                        handler.post {
                            progressBar.isVisible = false
                            when {
                                errorType != null -> toggleEmptyScreen(errorType)

                                foundTracks != null -> {
                                    tracks.clear()
                                    tracks.addAll(foundTracks)
                                    trackListAdapter.notifyDataSetChanged()
                                    rwTrackList.isVisible = true
                                    rwTrackList.layoutManager?.smoothScrollToPosition(
                                        rwTrackList, null, 0
                                    )
                                    toggleEmptyScreen(null)
                                }
                            }
                        }
                    }
                })
        }
    }

    private fun toggleEmptyScreen(type: ErrorType?) {
        val shouldShow = type != null

        emptyScreen.isVisible = shouldShow
        rwTrackList.isVisible = !shouldShow

        if (shouldShow) {
            findViewById<ImageView>(R.id.empty_screen_image).setImageResource(type!!.imageId)
            findViewById<TextView>(R.id.empty_screen_text).setText(type.messageId)

            val buttonRetry = findViewById<MaterialButton>(R.id.empty_screen_button)
            buttonRetry.isVisible = type == ErrorType.NoInternet
            buttonRetry.setOnClickListener {
                loadData()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchField.removeTextChangedListener(textWatcher)
    }
}