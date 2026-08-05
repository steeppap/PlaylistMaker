package com.example.playlistmaker.player.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.player.data.db.dao.TrackDao
import com.example.playlistmaker.player.data.db.entity.TrackEntity

@Database(version = 2, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun trackDao(): TrackDao
    
}
