package com.example.playlistmaker.settings.domain.api

interface ThemeInteractor {
    fun setDarkModeEnabled(enabled: Boolean)
    fun isDarkModeEnabled(): Boolean
}
