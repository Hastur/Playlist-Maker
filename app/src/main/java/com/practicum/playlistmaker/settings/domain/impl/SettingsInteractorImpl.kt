package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {

    override fun checkDarkThemeEnabled(): Boolean = repository.checkDarkThemeEnabled()

    override fun switchTheme(isDarkThemeEnabled: Boolean) =
        repository.switchTheme(isDarkThemeEnabled)

    override fun shareApp() = repository.shareApp()

    override fun sendMail() = repository.sendMail()

    override fun openUserAgreement() = repository.openUserAgreement()
}