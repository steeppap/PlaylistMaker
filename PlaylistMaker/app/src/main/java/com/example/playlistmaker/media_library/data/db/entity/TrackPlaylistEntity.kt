package com.example.playlistmaker.media_library.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_playlist_table")
data class TrackPlaylistEntity(
    @PrimaryKey
    val trackId: Long?,
    val artworkUrl100: String?,
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTimeMillis: Int,
    val previewUrl: String?,
    val addedAt: Long = 0
)

