package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl

class App : Application() {
    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
    }

    private lateinit var themeInteractor: ThemeInteractor
    private lateinit var sharedPrefs: SharedPrefsRepository
    private lateinit var themeRepository: ThemeRepository

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = Creator.createSharedPrefs(this)
        themeInteractor = Creator.provideThemeInteractor(this)
        themeInteractor.setDarkModeEnabled(sharedPrefs.getSetting(KEY_DARK_THEME) as Boolean)

    }
}
