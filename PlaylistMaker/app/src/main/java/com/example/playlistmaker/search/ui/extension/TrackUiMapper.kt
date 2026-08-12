package com.example.playlistmaker.search.ui.extension

import com.example.playlistmaker.media_library.domain.models.TrackPlaylist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.TrackUiModel

object TrackUiMapper {
    fun trackToTrackUiModel(track: Track): TrackUiModel{
        return TrackUiModel(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite
        )
    }
    fun trackUiModelToTrack(trackUiModel: TrackUiModel?): Track{
        return Track(
            trackName = trackUiModel?.trackName,
            artistName = trackUiModel?.artistName,
            trackTimeMillis = trackUiModel?.trackTimeMillis ?: 0,
            artworkUrl100 = trackUiModel?.artworkUrl100,
            trackId = trackUiModel?.trackId,
            collectionName = trackUiModel?.collectionName,
            releaseDate = trackUiModel?.releaseDate,
            primaryGenreName = trackUiModel?.primaryGenreName,
            country = trackUiModel?.country,
            previewUrl = trackUiModel?.previewUrl,
            isFavorite = trackUiModel?.isFavorite ?: false
        )
    }
    
    fun trackUiModelToTrackPlaylist(trackPlaylistUi: TrackUiModel?): TrackPlaylist {
        return TrackPlaylist(
            trackId = trackPlaylistUi?.trackId,
            artworkUrl100 = trackPlaylistUi?.artworkUrl100,
            trackName = trackPlaylistUi?.trackName,
            artistName = trackPlaylistUi?.artistName,
            collectionName = trackPlaylistUi?.collectionName,
            releaseDate = trackPlaylistUi?.releaseDate,
            primaryGenreName = trackPlaylistUi?.primaryGenreName,
            country = trackPlaylistUi?.country,
            trackTimeMillis = trackPlaylistUi?.trackTimeMillis ?: 0,
            previewUrl = trackPlaylistUi?.previewUrl
        )
    }
}
