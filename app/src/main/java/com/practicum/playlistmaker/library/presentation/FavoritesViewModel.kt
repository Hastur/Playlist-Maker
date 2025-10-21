package com.practicum.playlistmaker.library.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.library.presentation.models.FavoritesScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {

    private val _screenStateFlow =
        MutableStateFlow<FavoritesScreenState>(FavoritesScreenState.Loading)

    fun getScreenStateFlow(): StateFlow<FavoritesScreenState> = _screenStateFlow.asStateFlow()

    fun getFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { result ->
                if (result.isNotEmpty()) _screenStateFlow.update {
                    FavoritesScreenState.Content(result)
                }
                else _screenStateFlow.update { FavoritesScreenState.Empty }
            }
        }
    }
}