package com.practicum.playlistmaker.search.track_search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.models.ErrorType
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search.presentation.models.SearchScreenState
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryInteractor

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val searchInteractor = Creator.provideSearchInteractor()
                val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
                SearchViewModel(searchInteractor, searchHistoryInteractor)
            }
        }
    }

    private var screenStateLiveData = MutableLiveData<SearchScreenState>()
    fun getScreenStateLiveData(): LiveData<SearchScreenState> = screenStateLiveData

    private var historyLiveData = MutableLiveData<List<Track>>()
    fun getHistoryLiveData(): LiveData<List<Track>> = historyLiveData

    init {
        getHistory()
    }

    fun searchTrack(searchQuery: String) {
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