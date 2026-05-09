package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun setDarkModeEnabled(enabled: Boolean)
    fun isDarkModeEnabled(): Boolean
}