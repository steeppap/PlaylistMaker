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
    
    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    fun getPlaylistById(playlistId: Long): Flow<PlaylistEntity?>
    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    fun getPlaylistByIdSync(playlistId: Long): PlaylistEntity?
    
    @Query("UPDATE playlist_table SET title = :title, description = :description, coverPath=:coverPath WHERE id = :playlistId")
    fun updatePlaylistInfo(playlistId: Long, title: String, description: String?, coverPath: String?)
    @Query("SELECT * FROM playlist_table WHERE id != :currentPlaylistId ")
    fun getAllPlaylistsWithoutCurrentPlaylist(currentPlaylistId: Long): List<PlaylistEntity>
}
