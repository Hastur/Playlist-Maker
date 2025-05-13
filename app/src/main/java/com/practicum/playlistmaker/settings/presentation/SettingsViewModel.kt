package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsInteractor = Creator.provideSettingsInteractor()
                val sharingInteractor = Creator.provideSharingInteractor()
                SettingsViewModel(settingsInteractor, sharingInteractor)
            }
        }
    }

    private var darkThemeEnabledLiveData = MutableLiveData<Boolean>()
    fun getDarkThemeEnabledLiveData(): LiveData<Boolean> = darkThemeEnabledLiveData

    init {
        checkDarkThemeEnabled()
    }

    private fun checkDarkThemeEnabled() {
        darkThemeEnabledLiveData.value = settingsInteractor.checkDarkThemeEnabled()
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        settingsInteractor.switchTheme(isDarkThemeEnabled)
        darkThemeEnabledLiveData.value = isDarkThemeEnabled
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun sendMail(address: String, subject: String, text: String) {
        sharingInteractor.sendMail(address, subject, text)
    }

    fun openUserAgreement() {
        sharingInteractor.openUserAgreement()
    }
}