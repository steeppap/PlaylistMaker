package com.example.playlistmaker.settings.domain.api

interface ThemeRepository {
    fun setDarkModeEnabled(enabled: Boolean)
    fun isDarkModeEnabled(): Boolean
}