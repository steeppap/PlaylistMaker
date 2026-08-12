package com.example.playlistmaker.media_library.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media_library.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun createPlaylist(playlist: PlaylistEntity)
    
    @Query("DELETE FROM playlist_table WHERE id = :playlistId")
    suspend fun removePlaylistById(playlistId: Long)
    
    @Query("UPDATE playlist_table SET tracksIds = :newTracksJson, tracksCount = :count WHERE id = :playlistId")
    suspend fun updateTrackIdsInPlaylist(playlistId: Long, newTracksJson: String, count: Int)
    
    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    
}
