package com.example.playlistmaker.media_library.data.extension

import com.example.playlistmaker.media_library.data.db.entity.TrackPlaylistEntity
import com.example.playlistmaker.media_library.domain.models.TrackPlaylist

object TracksPlaylistMapper {
    fun trackListEntityToTrackList(trackListEntity: List<TrackPlaylistEntity>): List<TrackPlaylist> {
        return trackListEntity.map { trackListEntity ->
            TrackPlaylistMapper.trackPlaylistEntityToTrackPlaylist(trackListEntity)
        }
    }
}
