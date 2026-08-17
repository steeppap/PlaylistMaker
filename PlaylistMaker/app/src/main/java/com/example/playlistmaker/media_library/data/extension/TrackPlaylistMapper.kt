package com.example.playlistmaker.media_library.data.extension

import com.example.playlistmaker.media_library.data.db.entity.TrackPlaylistEntity
import com.example.playlistmaker.media_library.domain.models.TrackPlaylist

object TrackPlaylistMapper {
    fun trackPlaylistToTrackPlaylistEntity(trackPlaylist: TrackPlaylist): TrackPlaylistEntity {
        return TrackPlaylistEntity(
            trackId = trackPlaylist.trackId,
            artworkUrl100 = trackPlaylist.artworkUrl100,
            trackName = trackPlaylist.trackName,
            artistName = trackPlaylist.artistName,
            collectionName = trackPlaylist.collectionName,
            releaseDate = trackPlaylist.releaseDate,
            primaryGenreName = trackPlaylist.primaryGenreName,
            country = trackPlaylist.country,
            trackTimeMillis = trackPlaylist.trackTimeMillis,
            previewUrl = trackPlaylist.previewUrl
        )
    }
    
    fun trackPlaylistEntityToTrackPlaylist(trackPlaylistEntity: TrackPlaylistEntity): TrackPlaylist {
        return TrackPlaylist(
            trackId = trackPlaylistEntity.trackId,
            artworkUrl100 = trackPlaylistEntity.artworkUrl100,
            trackName = trackPlaylistEntity.trackName,
            artistName = trackPlaylistEntity.artistName,
            collectionName = trackPlaylistEntity.collectionName,
            releaseDate = trackPlaylistEntity.releaseDate,
            primaryGenreName = trackPlaylistEntity.primaryGenreName,
            country = trackPlaylistEntity.country,
            trackTimeMillis = trackPlaylistEntity.trackTimeMillis,
            previewUrl = trackPlaylistEntity.previewUrl
        )
    }
}
