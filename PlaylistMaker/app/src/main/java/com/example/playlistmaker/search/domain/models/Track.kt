package com.example.playlistmaker.search.domain.models

data class Track(
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: Int,
    val artworkUrl100: String?,
    val trackId: Long?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
)
