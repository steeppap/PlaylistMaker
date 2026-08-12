package com.example.playlistmaker.player.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.player.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToFavorite(track: TrackEntity)
    
    @Query("DELETE FROM track_table WHERE trackId = :trackId")
    suspend fun removeFromFavoriteById(trackId: Long?): Int
    
    @Query("SELECT * FROM track_table ORDER BY addedAt DESC")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>
    
    @Query("SELECT trackId FROM track_table")
    suspend fun getFavoriteTracksId(): List<Long>
    
    @Query("SELECT COUNT(*) FROM track_table WHERE trackId = :trackId")
    suspend fun isInFavoriteById(trackId: Long): Int
}
