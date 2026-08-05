package com.example.playlistmaker.player.data.impl

import com.example.playlistmaker.player.data.db.AppDatabase
import com.example.playlistmaker.player.data.extension.TrackEntityListMapper
import com.example.playlistmaker.player.data.extension.TrackEntityMapper
import com.example.playlistmaker.player.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase
) : FavoriteTracksRepository {
    
    override suspend fun addToFavorite(track: Track) {
        val currentTime = System.currentTimeMillis()
        
        val trackEntity = TrackEntityMapper.trackToTrackEntity(track, currentTime)
        appDatabase.trackDao().addToFavorite(trackEntity)
    }
    
    override suspend fun removeFromFavorite(track: Track) {
        appDatabase.trackDao().removeFromFavoriteById(track.trackId)
    }
    
    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        appDatabase.trackDao().getFavoriteTracks()
            .collect { trackEntities ->
                val tracks = TrackEntityListMapper.trackEntityListToTrackList(trackEntities)
                emit(tracks)
            }
    }
    
    override suspend fun isInFavoriteById(id: Long): Int {
        return appDatabase.trackDao().isInFavoriteById(id)
    }
}
