package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.settings.domain.api.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPrefs: SharedPreferences
) : ThemeRepository {

    override fun setDarkModeEnabled(enabled: Boolean) {
        sharedPrefs.edit {
            putBoolean(KEY_DARK_THEME, enabled)
        }
        val mode =
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun isDarkModeEnabled(): Boolean {
        return sharedPrefs.getBoolean(KEY_DARK_THEME, false)
    }
    companion object{
        private const val KEY_DARK_THEME = "dark_theme"
    }
}
