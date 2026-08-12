package com.example.playlistmaker.media_library.domain.db

import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.domain.models.TrackPlaylist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(title: String, desc: String?, coverPath: String?)
    
    suspend fun removePlaylistById(playlistId: Long)
    
    suspend fun updateTrackIdsInPlaylist(playlistId: Long, newTracksJson: String, count: Int)
    
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: TrackPlaylist, playlist: Playlist): Boolean
    }
