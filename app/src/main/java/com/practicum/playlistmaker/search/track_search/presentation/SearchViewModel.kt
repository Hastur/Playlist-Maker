package com.practicum.playlistmaker.search.track_search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search.presentation.models.SearchScreenState
import com.practicum.playlistmaker.search.track_search.presentation.models.SelectedTrack
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.util.Utils
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    private var selectedTrackLiveData = MutableLiveData<SelectedTrack>()
    fun getSelectedTrackLiveData(): LiveData<SelectedTrack> = selectedTrackLiveData

    private val searchDebounce: (String) -> Unit

    init {
        searchDebounce = debounce(DEBOUNCE_DELAY, viewModelScope, true) { input ->
            searchTrack(input)
        }

        getHistory()
    }

    fun searchWithDebounce(searchInput: String) {
        searchDebounce(searchInput)
    }

    private fun searchTrack(searchInput: String) {
        screenStateLiveData.value = SearchScreenState.Loading
        searchInteractor.searchTrack(searchInput, object : SearchInteractor.TrackConsumer {
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
        if (openPlayerDebounce()) selectedTrackLiveData.value =
            SelectedTrack(Utils().serializeToJson(track), true)
    }

    fun onTrackOpened(track: SelectedTrack) {
        selectedTrackLiveData.value = track.copy(needOpen = false)
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
            viewModelScope.launch {
                delay(DEBOUNCE_DELAY)
                isClickAllowed = true
            }
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