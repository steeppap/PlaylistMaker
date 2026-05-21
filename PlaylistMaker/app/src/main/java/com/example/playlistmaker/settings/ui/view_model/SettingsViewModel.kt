package com.example.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val themeInteractor: ThemeInteractor,
) : ViewModel() {
    fun setNecessaryTheme(){
        val enabled = themeInteractor.isDarkModeEnabled()
        themeInteractor.setDarkModeEnabled(!enabled)
    }
    fun shareApp(){
        sharingInteractor.shareApp()
    }
    fun openUserAgreement(){
        sharingInteractor.openTerms()
    }
    fun writeToSupport(){
        sharingInteractor.writeToSupport()
    }

    companion object {
        fun getFactory(
            sharingInteractor: SharingInteractor,
            themeInteractor: ThemeInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor, themeInteractor)
            }
        }
    }
}
