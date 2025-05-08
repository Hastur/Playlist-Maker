package com.practicum.playlistmaker

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.application = applicationContext as Application

        val themeSettings = Creator.provideSettingsInteractor()
        themeSettings.switchTheme(themeSettings.checkDarkThemeEnabled())
    }
}