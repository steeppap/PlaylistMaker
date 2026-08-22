package com.example.playlistmaker.player.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media_library.data.db.dao.PlaylistDao
import com.example.playlistmaker.media_library.data.db.dao.TrackPlaylistDao
import com.example.playlistmaker.media_library.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media_library.data.db.entity.TrackPlaylistEntity
import com.example.playlistmaker.player.data.db.dao.TrackDao
import com.example.playlistmaker.player.data.db.entity.TrackEntity

@Database(version = 3, entities = [TrackEntity::class, PlaylistEntity::class, TrackPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackPlaylistDao(): TrackPlaylistDao
    
}
