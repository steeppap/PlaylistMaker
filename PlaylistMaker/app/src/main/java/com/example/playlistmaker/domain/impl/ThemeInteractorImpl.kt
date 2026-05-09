package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {
    override fun setDarkModeEnabled(enabled: Boolean) {
        themeRepository.setDarkModeEnabled(enabled)
    }
    override fun isDarkModeEnabled(): Boolean = themeRepository.isDarkModeEnabled()
}
