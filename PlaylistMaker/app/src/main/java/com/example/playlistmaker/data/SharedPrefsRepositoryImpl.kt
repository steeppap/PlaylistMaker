package com.example.playlistmaker.data

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import androidx.core.content.edit

class SharedPrefsRepositoryImpl(private val context: Context) : SharedPrefsRepository {

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val KEY_DARK_THEME = "dark_theme"
    }

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun saveSetting(key: String, value: Any) {
        if (value is Boolean) {
            sharedPrefs.edit {
                putBoolean(key, value)
            }
        } else {
            sharedPrefs.edit {
                putString(key, value as String)
            }
        }
    }

    override fun getSetting(key: String): Any? {
        return if (key == KEY_DARK_THEME) {
            sharedPrefs.getBoolean(key, false)
        } else sharedPrefs.getString(key, null)
    }

    override fun clearSetting(key: String) {
        sharedPrefs.edit { remove(key) }
    }
}
