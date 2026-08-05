package com.example.playlistmaker.player.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun addToFavorite(track: Track)
    suspend fun removeFromFavorite(track: Track)
    suspend fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun isInFavoriteById(id: Long): Boolean
}
