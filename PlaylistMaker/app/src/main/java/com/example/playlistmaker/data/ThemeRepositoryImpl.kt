package com.example.playlistmaker.data

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPrefs: SharedPrefsRepository,
) : ThemeRepository {
    companion object{
        private const val KEY_DARK_THEME = "dark_theme"
    }

    override fun setDarkModeEnabled(enabled: Boolean) {

        sharedPrefs.saveSetting(KEY_DARK_THEME, enabled)

        val mode =
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun isDarkModeEnabled(): Boolean {
        return sharedPrefs.getSetting(KEY_DARK_THEME) as Boolean
    }
}
