package com.example.playlistmaker.media_library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media_library.data.db.entity.TrackPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTrackToDB(track: TrackPlaylistEntity)
    
    @Query("SELECT * FROM track_playlist_table WHERE trackId IN (:trackIds) ORDER BY addedAt DESC")
    fun getTracksByIds(trackIds: List<Long>?): Flow<List<TrackPlaylistEntity>>
    @Query("DELETE FROM track_playlist_table WHERE trackId = :trackId")
    suspend fun deleteTrackFromDB(trackId: Long)
}
