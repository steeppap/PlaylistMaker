package com.example.playlistmaker.media_library.ui.viewmodels

import com.example.playlistmaker.search.ui.models.TrackUiModel

sealed class FavoriteState {
    object Empty : FavoriteState()
    data class WithTracks(val tracks: List<TrackUiModel>) : FavoriteState()
}
