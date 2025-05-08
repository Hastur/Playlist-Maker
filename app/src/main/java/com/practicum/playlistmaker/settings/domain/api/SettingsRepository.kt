package com.practicum.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun checkDarkThemeEnabled(): Boolean
    fun switchTheme(isDarkThemeEnabled: Boolean)
    fun shareApp()
    fun sendMail()
    fun openUserAgreement()
}