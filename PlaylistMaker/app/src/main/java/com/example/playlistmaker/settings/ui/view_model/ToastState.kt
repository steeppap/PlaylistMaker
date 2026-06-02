package com.example.playlistmaker.settings.ui.view_model

sealed class ToastState {
    data class ShowToast(val message: String) : ToastState()
    object HideToast : ToastState()
}
