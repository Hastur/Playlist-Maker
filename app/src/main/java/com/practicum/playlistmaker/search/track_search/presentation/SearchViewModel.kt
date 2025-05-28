package com.practicum.playlistmaker.search.track_search.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search.presentation.models.SearchScreenState
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.util.Utils

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_DELAY = 2000L
    }

    private var screenStateLiveData = MutableLiveData<SearchScreenState>()
    fun getScreenStateLiveData(): LiveData<SearchScreenState> = screenStateLiveData

    private var historyLiveData = MutableLiveData<List<Track>>()
    fun getHistoryLiveData(): LiveData<List<Track>> = historyLiveData

    init {
        getHistory()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTrack() }
    private lateinit var searchQuery: String

    fun searchWithDebounce(searchInput: String) {
        searchQuery = searchInput
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, DEBOUNCE_DELAY)
    }

    private fun searchTrack() {
        screenStateLiveData.value = SearchScreenState.Loading
        searchInteractor.searchTrack(searchQuery, object : SearchInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>?, errorType: ErrorType?) {
                when {
                    foundTracks != null -> screenStateLiveData.postValue(
                        SearchScreenState.Content(foundTracks)
                    )

                    errorType != null -> screenStateLiveData.postValue(
                        SearchScreenState.Error(errorType)
                    )
                }
            }
        })
    }

    fun performTrackClick(track: Track) {
        if (openPlayerDebounce()) screenStateLiveData.value =
            SearchScreenState.OpenPlayer(Utils().serializeToJson(track))
    }

    fun setInitialState() {
        screenStateLiveData.value = SearchScreenState.Initial
    }

    fun setFocusedState() {
        screenStateLiveData.value =
            SearchScreenState.Focused(!historyLiveData.value.isNullOrEmpty())
    }

    fun setTypingState() {
        screenStateLiveData.value = SearchScreenState.Typing
    }

    private var isClickAllowed = true
    private fun openPlayerDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, DEBOUNCE_DELAY)
        }
        return current
    }

    private fun getHistory() {
        historyLiveData.value = searchHistoryInteractor.getTracks()
    }

    fun addToHistory(track: Track) {
        historyLiveData.value = searchHistoryInteractor.addTrack(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        historyLiveData.value = listOf()
    }
}