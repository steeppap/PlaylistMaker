package com.example.playlistmaker.media_library.domain.models

data class TrackPlaylist(
    val trackId: Long?,
    val artworkUrl100: String?,
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTimeMillis: Int,
    val previewUrl: String?
)
