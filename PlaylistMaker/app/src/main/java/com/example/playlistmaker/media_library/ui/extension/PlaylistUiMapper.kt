package com.example.playlistmaker.media_library.ui.extension

import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.ui.models.PlaylistUi

object PlaylistUiMapper {
    fun playlistToPlaylistUi(playlist: Playlist): PlaylistUi {
        return PlaylistUi(
            id = playlist.id ?: 0L,
            title = playlist.title,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracksIds = playlist.tracksIds,
            tracksCount = playlist.tracksCount
        )
    }
    
    fun playlistUiToPlaylist(playlistUi: PlaylistUi): Playlist {
        return Playlist(
            id = playlistUi.id,
            title = playlistUi.title,
            description = playlistUi.description,
            coverPath = playlistUi.coverPath,
            tracksIds = playlistUi.tracksIds,
            tracksCount = playlistUi.tracksCount
        )
    }
}
