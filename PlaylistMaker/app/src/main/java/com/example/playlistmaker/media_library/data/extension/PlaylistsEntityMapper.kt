package com.example.playlistmaker.media_library.data.extension

import com.example.playlistmaker.media_library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media_library.domain.models.Playlist

object PlaylistsEntityMapper {
    fun playlistsListEntityToPlaylistsList(playlistsListEntity: List<PlaylistEntity>): List<Playlist> {
        return playlistsListEntity.map { playlistEntity ->
            PlaylistEntityMapper.playlistEntityToPlaylist(playlistEntity)
        }
    }
}
