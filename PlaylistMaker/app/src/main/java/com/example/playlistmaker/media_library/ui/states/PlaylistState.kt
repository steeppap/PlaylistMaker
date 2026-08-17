package com.example.playlistmaker.media_library.ui.states

import com.example.playlistmaker.media_library.ui.models.PlaylistUi

sealed class PlaylistState {
    data class Content(val playlists: List<PlaylistUi>) : PlaylistState()
    object Empty : PlaylistState()
}
