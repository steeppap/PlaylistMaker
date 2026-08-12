package com.example.playlistmaker.media_library.domain.impl

import com.example.playlistmaker.media_library.domain.db.PlaylistInteractor
import com.example.playlistmaker.media_library.domain.db.PlaylistRepository
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.domain.models.TrackPlaylist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val repository: PlaylistRepository) : PlaylistInteractor {
    
    override suspend fun createPlaylist(
        title: String,
        desc: String?,
        coverPath: String?
    ) {
        repository.createPlaylist(title, desc, coverPath)
    }
    
    override suspend fun removePlaylistById(playlistId: Long) {
        repository.removePlaylistById(playlistId)
    }
    
    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }
    
    override suspend fun addTrackToPlaylist(
        track: TrackPlaylist,
        playlist: Playlist
    ): Boolean {
        return repository.addTrackToPlaylist(track, playlist)
    }
}
