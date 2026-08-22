package com.example.playlistmaker.media_library.data.extension

import com.example.playlistmaker.media_library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media_library.domain.models.Playlist

object PlaylistEntityMapper {
    
    fun playlistToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id ?: 0L,
            title = playlist.title,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracksIds = playlist.tracksIds,
            tracksCount = playlist.tracksCount
        )
    }
    
    fun playlistEntityToPlaylist(playlistEntity: PlaylistEntity?): Playlist {
        return Playlist(
            id = playlistEntity?.id,
            title = playlistEntity?.title ?: "",
            description = playlistEntity?.description,
            coverPath = playlistEntity?.coverPath,
            tracksIds = playlistEntity?.tracksIds,
            tracksCount = playlistEntity?.tracksCount ?: 0
        )
    }
}
