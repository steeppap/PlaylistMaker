package com.example.playlistmaker.media_library.domain.db

import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.domain.models.TrackPlaylist
import com.example.playlistmaker.media_library.ui.models.PlaylistUi
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(title: String, desc: String?, coverPath: String?)
    
    suspend fun removePlaylistById(playlistId: Long)
    
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(track: TrackPlaylist, playlist: Playlist): Boolean
    suspend fun removeTrackFromPlaylist(trackId: Long, playlistUi: PlaylistUi)
    fun getPlaylistById(playlistId: Long):Flow<PlaylistUi>
    fun getTracksByIds(tracksIds: String?):Flow<List<TrackPlaylist>>
    suspend fun updatePlaylistInfo(playlistId: Long, title: String, description: String?, coverPath: String?)
}
