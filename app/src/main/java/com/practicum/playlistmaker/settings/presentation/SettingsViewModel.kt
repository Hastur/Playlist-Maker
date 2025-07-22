package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

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

    fun shareApp(message: String) {
        sharingInteractor.shareMessage(message)
    }

    fun sendMail(address: String, subject: String, text: String) {
        sharingInteractor.sendMail(address, subject, text)
    }

    fun openUserAgreement() {
        sharingInteractor.openUserAgreement()
    }
}