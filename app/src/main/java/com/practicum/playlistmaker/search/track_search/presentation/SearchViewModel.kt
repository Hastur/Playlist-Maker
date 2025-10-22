package com.practicum.playlistmaker.search.track_search.presentation

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_DELAY = 2000L
    }

    private val _screenStateFlow =
        MutableStateFlow<SearchScreenState>(SearchScreenState.Content(listOf()))

    fun getScreenStateFlow(): StateFlow<SearchScreenState> = _screenStateFlow.asStateFlow()

    private var historyLiveData = MutableLiveData<List<Track>>()

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
        _screenStateFlow.update { SearchScreenState.Initial }
        searchDebounce(searchInput)
    }

    private fun searchTrack(searchInput: String) {
        _screenStateFlow.update { SearchScreenState.Loading }

        viewModelScope.launch {
            searchInteractor
                .searchTrack(searchInput)
                .collect { pair ->
                    when {
                        pair.first != null -> {
                            _screenStateFlow.update { SearchScreenState.Content(pair.first!!) }
                        }

                        pair.second != null -> {
                            _screenStateFlow.update { SearchScreenState.Error(pair.second!!) }
                        }
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
        _screenStateFlow.update { SearchScreenState.Initial }
    }

    fun setFocused() {
        if (!historyLiveData.value.isNullOrEmpty()) {
            _screenStateFlow.update {
                SearchScreenState.Content(historyLiveData.value!!, true)
            }
        }
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
        setInitialState()
        historyLiveData.value = listOf()
    }
}