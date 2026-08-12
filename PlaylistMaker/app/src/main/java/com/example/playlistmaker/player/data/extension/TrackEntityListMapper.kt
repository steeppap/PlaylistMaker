package com.example.playlistmaker.player.data.extension

import com.example.playlistmaker.player.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

object TrackEntityListMapper {
    
    fun trackEntityListToTrackList(trackEntityList: List<TrackEntity>): List<Track> {
        return trackEntityList.map { trackEntity ->
            TrackEntityMapper.trackEntityToTrack(trackEntity)
        }
    }
}
