package com.example.playlistmaker.domain.api

interface ThemeInteractor {
    fun setDarkModeEnabled(enabled: Boolean)
    fun isDarkModeEnabled(): Boolean
}
