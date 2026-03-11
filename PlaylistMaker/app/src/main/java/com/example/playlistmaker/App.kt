package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    companion object{
        const val PREFS_NAME = "app_prefs"
        private const val KEY_DARK_THEME = "dark_theme"
    }
    private lateinit var prefs: SharedPreferences
    var statusDarkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        statusDarkTheme = prefs.getBoolean(KEY_DARK_THEME, false)
        switchTheme(statusDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        statusDarkTheme = darkThemeEnabled
        prefs.edit().putBoolean(KEY_DARK_THEME,statusDarkTheme).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
