package com.example.playlistmaker.media_library.ui.extension

import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.ui.models.PlaylistUi

object PlaylistsUiMapper {
    fun playlistsToPlaylistsUi(playlists: List<Playlist>): List<PlaylistUi> {
        return playlists.map { playlist ->
            PlaylistUiMapper.playlistToPlaylistUi(playlist)
        }
    }
}
