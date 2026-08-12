package com.example.playlistmaker.media_library.domain.models

import kotlin.Int
import kotlin.Long
import kotlin.String

data class Playlist(
    val id: Long?,
    val title: String,
    val description: String?,
    val coverPath: String?,
    val tracksIds: String?,
    val tracksCount: Int
) {
    constructor(title: String, description: String?, coverPath: String?) : this(
        id = 0L,
        title = title,
        description = description,
        coverPath = coverPath,
        tracksIds = "",
        tracksCount = 0
    )
}


