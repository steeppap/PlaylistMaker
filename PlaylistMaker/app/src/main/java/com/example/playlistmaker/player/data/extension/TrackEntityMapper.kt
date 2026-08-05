package com.example.playlistmaker.player.data.extension

import com.example.playlistmaker.player.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

object TrackEntityMapper {
    
    fun trackToTrackEntity(track: Track, addedAt: Long): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            addedAt = addedAt
        )
    }
    
    fun trackEntityToTrack(trackEntity: TrackEntity): Track {
        return Track(
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            trackTimeMillis = trackEntity.trackTimeMillis,
            artworkUrl100 = trackEntity.artworkUrl100,
            trackId = trackEntity.trackId,
            collectionName = trackEntity.collectionName,
            releaseDate = trackEntity.releaseDate,
            primaryGenreName = trackEntity.primaryGenreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl
        )
    }
}
