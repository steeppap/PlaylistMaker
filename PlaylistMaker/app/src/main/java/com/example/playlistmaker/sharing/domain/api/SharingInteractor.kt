package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.settings.ui.view_model.ToastState

interface SharingInteractor {
    fun shareApp()
    fun openTerms()
    fun writeToSupport(onUiStateChanged: (ToastState) -> Unit)
}
