package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private var _darkThemeFlow = MutableStateFlow(false)
    fun getDarkThemeFlow(): StateFlow<Boolean> = _darkThemeFlow.asStateFlow()

    init {
        checkDarkThemeEnabled()
    }

    private fun checkDarkThemeEnabled() {
        _darkThemeFlow.update { settingsInteractor.checkDarkThemeEnabled() }
    }

    fun switchTheme(isDarkThemeEnabled: Boolean) {
        settingsInteractor.switchTheme(isDarkThemeEnabled)
        _darkThemeFlow.update { isDarkThemeEnabled }
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