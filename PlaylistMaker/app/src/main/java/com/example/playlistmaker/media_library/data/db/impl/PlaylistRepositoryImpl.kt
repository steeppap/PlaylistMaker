package com.example.playlistmaker.media_library.data.db.impl

import android.util.Log
import androidx.room.concurrent.AtomicBoolean
import com.example.playlistmaker.media_library.data.extension.PlaylistEntityMapper
import com.example.playlistmaker.media_library.data.extension.PlaylistsEntityMapper
import com.example.playlistmaker.media_library.data.extension.TrackPlaylistMapper
import com.example.playlistmaker.media_library.data.extension.TracksPlaylistMapper
import com.example.playlistmaker.media_library.domain.db.PlaylistRepository
import com.example.playlistmaker.media_library.domain.models.Playlist
import com.example.playlistmaker.media_library.domain.models.TrackPlaylist
import com.example.playlistmaker.player.data.db.AppDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
        val currentPlaylist = appDatabase.playlistDao().getPlaylistByIdSync(playlistId)
            ?: throw IllegalStateException("Плейлист не найден")
        val type = object : TypeToken<List<Long>?>() {}.type
        val parsedList: List<Long> = gson.fromJson(currentPlaylist.tracksIds, type) ?: emptyList()
        
        for (trackId in parsedList) {
            val isTrackInOtherPlaylists = checkTrackInOtherPlaylist(trackId, playlistId)
            if (!isTrackInOtherPlaylists) {
                appDatabase.trackPlaylistDao().deleteTrackFromDB(trackId)
            }
        }
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
                val playlists =
                    PlaylistsEntityMapper.playlistsListEntityToPlaylistsList(playlistEntity)
                emit(playlists)
            }
    }
    
    override suspend fun addTrackToPlaylist(
        track: TrackPlaylist,
        playlist: Playlist
    ): Boolean {
        val currentTime = System.currentTimeMillis()
        val trackEntity = TrackPlaylistMapper.trackPlaylistToTrackPlaylistEntity(track, currentTime)
        
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
    
    override suspend fun removeTrackFromPlaylist(trackId: Long, playlist: Playlist) {
        val playlistEntity = PlaylistEntityMapper.playlistToPlaylistEntity(playlist)
        val type = object : TypeToken<List<Long>>() {}.type
        val parsedList: MutableList<Long> =
            gson.fromJson<MutableList<Long>>(playlistEntity.tracksIds, type).toMutableList()
        
        parsedList.remove(trackId)
        updateTrackIdsInPlaylist(
            playlistEntity.id,
            gson.toJson(parsedList),
            playlistEntity.tracksCount - 1
        )
        val isTrackInOtherPlaylists = checkTrackInOtherPlaylist(trackId, playlist.id!!)
        if (!isTrackInOtherPlaylists) {
            appDatabase.trackPlaylistDao().deleteTrackFromDB(trackId)
        }
    }
    
    override fun getPlaylistById(playlistId: Long): Flow<Playlist> = flow {
        appDatabase.playlistDao().getPlaylistById(playlistId)
            .collect { playlistEntity ->
                val playlist = PlaylistEntityMapper.playlistEntityToPlaylist(playlistEntity)
                emit(playlist)
            }
    }
    
    override fun getTracksByIds(tracksIds: String?): Flow<List<TrackPlaylist>> = flow {
        val type = object : TypeToken<List<Long>>() {}.type
        
        val parsedList: List<Long>? = gson.fromJson(tracksIds, type)
        appDatabase.trackPlaylistDao().getTracksByIds(parsedList).collect { trackListEntity ->
            val tracksPlayList = TracksPlaylistMapper.trackListEntityToTrackList(trackListEntity)
            emit(tracksPlayList)
        }
    }
    
    override suspend fun updatePlaylistInfo(
        playlistId: Long,
        title: String,
        description: String?,
        coverPath: String?
    ) {
        appDatabase.playlistDao().updatePlaylistInfo(playlistId, title, description, coverPath)
    }
    
    private fun checkTrackInOtherPlaylist(trackId: Long, currentPlaylistId: Long): Boolean {
        val allPlaylists =
            appDatabase.playlistDao().getAllPlaylistsWithoutCurrentPlaylist(currentPlaylistId)
        val type = object : TypeToken<List<Long>>() {}.type
        
        for (playlist in allPlaylists) {
            if (playlist.tracksIds.isNullOrBlank()) continue
            val trackIdsInPlaylist: List<Long> = gson.fromJson(playlist.tracksIds, type)
            
            if (trackIdsInPlaylist.contains(trackId)) {
                return true
            }
        }
        return false
    }
}
