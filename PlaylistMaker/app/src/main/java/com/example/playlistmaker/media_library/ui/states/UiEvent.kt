package com.example.playlistmaker.media_library.ui.states

sealed class UiEvent {
    data class ShowToast(
        val message: String,
        val shouldHideBottomSheet: Boolean = false) : UiEvent()
}
