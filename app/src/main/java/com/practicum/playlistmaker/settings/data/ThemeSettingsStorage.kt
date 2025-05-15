package com.practicum.playlistmaker.settings.data

interface ThemeSettingsStorage {
    fun getDarkTheme(): Boolean
    fun saveThemeSettings(isDarkThemeEnabled: Boolean)
}