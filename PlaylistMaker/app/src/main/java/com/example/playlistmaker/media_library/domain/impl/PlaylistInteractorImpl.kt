package com.example.playlistmaker.media_library.domain.impl

import com.example.playlistmaker.media_library.domain.db.PlaylistInteractor
import com.example.playlistmaker.media_library.domain.db.PlaylistRepository
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.domain.models.TrackPlaylist
import com.example.playlistmaker.media_library.ui.extension.PlaylistUiMapper
import com.example.playlistmaker.media_library.ui.models.PlaylistUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
    
    override suspend fun removeTrackFromPlaylist(
        trackId: Long,
        playlistUi: PlaylistUi
    ) {
        val playlist = PlaylistUiMapper.playlistUiToPlaylist(playlistUi)
        repository.removeTrackFromPlaylist(trackId, playlist)
    }
    
    override fun getPlaylistById(playlistId: Long): Flow<PlaylistUi> = flow {
        repository.getPlaylistById(playlistId).collect {
            val playlistUi = PlaylistUiMapper.playlistToPlaylistUi(it)
            emit(playlistUi)
        }
    }
    
    override fun getTracksByIds(tracksIds: String?): Flow<List<TrackPlaylist>> {
        return repository.getTracksByIds(tracksIds)
    }
    
    override suspend fun updatePlaylistInfo(
        playlistId: Long,
        title: String,
        description: String?,
        coverPath: String?
    ) {
        repository.updatePlaylistInfo(playlistId, title, description, coverPath)
    }
}
