package com.practicum.playlistmaker.library.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.library.presentation.models.FavoritesScreenState
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private var favoritesLiveData =
        MutableLiveData<FavoritesScreenState>(FavoritesScreenState.Loading)

    fun getFavoritesStateLiveData(): LiveData<FavoritesScreenState> = favoritesLiveData

    fun getFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { result ->
                if (result.isNotEmpty()) favoritesLiveData.postValue(
                    FavoritesScreenState.Content(result)
                )
                else favoritesLiveData.postValue(FavoritesScreenState.Empty)
            }
        }
    }
}