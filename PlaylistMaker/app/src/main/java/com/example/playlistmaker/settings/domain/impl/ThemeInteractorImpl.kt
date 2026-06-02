package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.api.ThemeRepository

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {
    override fun setDarkModeEnabled(enabled: Boolean) {
        themeRepository.setDarkModeEnabled(enabled)
    }
    override fun isDarkModeEnabled(): Boolean = themeRepository.isDarkModeEnabled()
}