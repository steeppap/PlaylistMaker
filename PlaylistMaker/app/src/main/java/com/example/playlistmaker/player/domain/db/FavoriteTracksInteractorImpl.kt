package com.example.playlistmaker.player.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {
    override suspend fun addToFavorite(track: Track) {
        favoriteTracksRepository.addToFavorite(track)
    }
    
    override suspend fun removeFromFavorite(track: Track) {
        favoriteTracksRepository.removeFromFavorite(track)
    }
    
    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getFavoriteTracks()
    }
    
    override suspend fun isInFavoriteById(id: Long): Boolean {
        val count = favoriteTracksRepository.isInFavoriteById(id)
        return count > 0
    }
}
