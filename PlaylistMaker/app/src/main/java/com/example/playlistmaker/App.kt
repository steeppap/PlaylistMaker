package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    private val prefsName = "app_prefs"
    private val keyDarkTheme = "dark_theme"
    private lateinit var prefs: SharedPreferences
    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        darkTheme = prefs.getBoolean(keyDarkTheme, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        prefs.edit().putBoolean(keyDarkTheme,darkTheme).apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
