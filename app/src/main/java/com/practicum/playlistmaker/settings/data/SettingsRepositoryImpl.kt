package com.practicum.playlistmaker.settings.data

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val localStorage: ThemeSettingsStorage) : SettingsRepository {

    override fun checkDarkThemeEnabled(): Boolean = localStorage.getDarkTheme()

    override fun switchTheme(isDarkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(if (isDarkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        localStorage.saveThemeSettings(isDarkThemeEnabled)
    }
}