package com.example.playlistmaker.media_library.data.db.impl

import com.example.playlistmaker.media_library.data.extension.PlaylistEntityMapper
import com.example.playlistmaker.media_library.data.extension.PlaylistsEntityMapper
import com.example.playlistmaker.media_library.data.extension.TrackPlaylistMapper
import com.example.playlistmaker.media_library.domain.db.PlaylistRepository
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.domain.models.TrackPlaylist
import com.example.playlistmaker.player.data.db.AppDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(private val appDatabase: AppDatabase, private val gson: Gson) :
    PlaylistRepository {
    
    override suspend fun createPlaylist(title: String, desc: String?, coverPath: String?) {
        val playlist = Playlist(
            title = title,
            description = desc,
            coverPath = coverPath
        )
        val playlistEntity = PlaylistEntityMapper.playlistToPlaylistEntity(playlist)
        appDatabase.playlistDao().createPlaylist(playlistEntity)
    }
    
    override suspend fun removePlaylistById(playlistId: Long) {
        appDatabase.playlistDao().removePlaylistById(playlistId)
    }
    
    override suspend fun updateTrackIdsInPlaylist(
        playlistId: Long,
        newTracksJson: String,
        count: Int
    ) {
        appDatabase.playlistDao().updateTrackIdsInPlaylist(playlistId, newTracksJson, count)
    }
    
    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        appDatabase.playlistDao().getAllPlaylists()
            .collect { playlistEntity ->
                val playlist =
                    PlaylistsEntityMapper.playlistsListEntityToPlaylistsList(playlistEntity)
                emit(playlist)
            }
    }
    
    override suspend fun addTrackToPlaylist(
        track: TrackPlaylist,
        playlist: Playlist
    ): Boolean {
        val trackEntity = TrackPlaylistMapper.trackPlaylistToTrackPlaylistEntity(track)
        val type = object : TypeToken<List<Long>>() {}.type
        var trackInPlaylist: Boolean
        
        val trackIds: MutableList<Long> =
            if (playlist.tracksIds.isNullOrEmpty()) {
                mutableListOf()
            } else {
                val parsedList: List<Long>? = gson.fromJson(playlist.tracksIds, type)
                parsedList?.toMutableList() ?: mutableListOf()
            }
        
        if (track.trackId != null && track.trackId !in trackIds) {
            trackInPlaylist = false
            
            trackIds.add(track.trackId)
            
            appDatabase.trackPlaylistDao().addTrackToDB(trackEntity)
            
            updateTrackIdsInPlaylist(
                playlist.id!!,
                gson.toJson(trackIds),
                playlist.tracksCount + 1
            )
        } else {
            trackInPlaylist = true
        }
        return trackInPlaylist
    }
}
