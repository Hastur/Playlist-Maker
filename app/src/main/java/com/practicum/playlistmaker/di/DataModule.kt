package com.practicum.playlistmaker.di

import android.content.Context.MODE_PRIVATE
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.search.track_search.data.NetworkClient
import com.practicum.playlistmaker.search.track_search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.track_search.data.network.TrackSearchApi
import com.practicum.playlistmaker.search.track_search_history.data.SearchHistoryStorage
import com.practicum.playlistmaker.search.track_search_history.data.local.SharedPreferencesSearchHistoryStorage
import com.practicum.playlistmaker.settings.data.ThemeSettingsStorage
import com.practicum.playlistmaker.settings.data.local.SharedPreferencesThemeSettingsStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val SHARED_PREFERENCE_SEARCH_HISTORY = "SHARED_PREFERENCE_SEARCH_HISTORY"
private const val SHARED_PREFERENCE_DARK_THEME = "SHARED_PREFERENCE_DARK_THEME"

val dataModule = module {

    single<TrackSearchApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackSearchApi::class.java)
    }

    single(named(SHARED_PREFERENCE_SEARCH_HISTORY)) {
        androidContext().getSharedPreferences(SHARED_PREFERENCE_SEARCH_HISTORY, MODE_PRIVATE)
    }

    single<SearchHistoryStorage> {
        SharedPreferencesSearchHistoryStorage(
            get(named(SHARED_PREFERENCE_SEARCH_HISTORY)), get()
        )
    }

    single(named(SHARED_PREFERENCE_DARK_THEME)) {
        androidContext().getSharedPreferences(SHARED_PREFERENCE_DARK_THEME, MODE_PRIVATE)
    }

    single<ThemeSettingsStorage> {
        SharedPreferencesThemeSettingsStorage(
            get(named(SHARED_PREFERENCE_DARK_THEME)), androidContext()
        )
    }

    single<NetworkClient> { RetrofitNetworkClient(get(), androidContext()) }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    factory { Gson() }

    factory { MediaPlayer() }

}