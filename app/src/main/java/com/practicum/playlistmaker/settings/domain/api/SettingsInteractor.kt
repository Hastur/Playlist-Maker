package com.practicum.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun checkDarkThemeEnabled(): Boolean
    fun switchTheme(isDarkThemeEnabled: Boolean)
    fun shareApp()
    fun sendMail()
    fun openUserAgreement()
}