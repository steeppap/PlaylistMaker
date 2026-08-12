package com.example.playlistmaker.media_library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.media_library.data.db.entity.TrackPlaylistEntity
@Dao
interface TrackPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTrackToDB(track: TrackPlaylistEntity)
}
