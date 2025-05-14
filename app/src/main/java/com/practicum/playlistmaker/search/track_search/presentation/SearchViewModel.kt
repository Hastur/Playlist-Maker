package com.practicum.playlistmaker.search.track_search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.track_search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search.presentation.models.SearchScreenState
import com.practicum.playlistmaker.search.track_search_history.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.util.Resource
import com.practicum.playlistmaker.util.Utils

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
        when (val response = searchInteractor.searchTrack(searchQuery)) {
            is Resource.Success -> screenStateLiveData.postValue(response.data?.let {
                SearchScreenState.Content(it)
            })

            is Resource.Error -> screenStateLiveData.postValue(response.errorType?.let {
                SearchScreenState.Error(it)
            })
        }
    }

    fun performTrackClick(track: Track) {
        screenStateLiveData.value = SearchScreenState.OpenPlayer(Utils().serializeToJson(track))
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