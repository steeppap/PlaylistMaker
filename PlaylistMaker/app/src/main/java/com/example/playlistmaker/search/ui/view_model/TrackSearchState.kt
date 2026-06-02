package com.example.playlistmaker.search.ui.view_model

import com.example.playlistmaker.search.domain.models.Track

sealed class TrackSearchState {
    object Default: TrackSearchState()
    object Loading: TrackSearchState()
    object Success: TrackSearchState()
    object Empty : TrackSearchState()
    object Error : TrackSearchState()
    object History : TrackSearchState()
}
