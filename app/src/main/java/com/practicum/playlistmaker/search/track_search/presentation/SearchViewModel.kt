package com.practicum.playlistmaker.search.track_search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search.presentation.models.SearchScreenState
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.util.SingleLiveEvent
import com.practicum.playlistmaker.util.Utils
import com.practicum.playlistmaker.util.debounce
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

    private var selectedTrackSingleEvent = SingleLiveEvent<String>()
    fun getSelectedTrackSingleEvent(): SingleLiveEvent<String> = selectedTrackSingleEvent

    private val searchDebounce: (String) -> Unit
    private val trackOpenDebounce: (Boolean) -> Unit
    private var isClickAllowed = true

    init {
        searchDebounce = debounce(DEBOUNCE_DELAY, viewModelScope, true) { input ->
            if (input.isNotEmpty()) searchTrack(input)
        }

        trackOpenDebounce = debounce(DEBOUNCE_DELAY, viewModelScope, false) {
            isClickAllowed = it
        }

        getHistory()
    }

    fun searchWithDebounce(searchInput: String) {
        searchDebounce(searchInput)
    }

    private fun searchTrack(searchInput: String) {
        screenStateLiveData.value = SearchScreenState.Loading

        viewModelScope.launch {
            searchInteractor
                .searchTrack(searchInput)
                .collect { pair ->
                    when {
                        pair.first != null -> screenStateLiveData.postValue(
                            SearchScreenState.Content(pair.first!!)
                        )

                        pair.second != null -> screenStateLiveData.postValue(
                            SearchScreenState.Error(pair.second!!)
                        )
                    }
                }
        }
    }

    fun openTrackWithDebounce(track: Track) {
        if (isClickAllowed) {
            isClickAllowed = false
            selectedTrackSingleEvent.value = Utils().serializeToJson(track)
            trackOpenDebounce(true)
        }
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

    private fun getHistory() {
        viewModelScope.launch {
            historyLiveData.value = searchHistoryInteractor.getTracks()
        }
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch {
            historyLiveData.value = searchHistoryInteractor.addTrack(track)
        }
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        historyLiveData.value = listOf()
    }
}