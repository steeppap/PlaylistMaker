package com.example.playlistmaker.media_library.ui.models

data class PlaylistUi(
    val id: Long?,
    val title: String,
    val description: String?,
    val coverPath: String?,
    val tracksIds: String?,
    val tracksCount: Int
)
