package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.player.data.db.AppDatabase
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.ITunesRequest
import com.example.playlistmaker.search.data.dto.ITunesResponse
import com.example.playlistmaker.search.data.extension.TrackDtoMapper
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {
    
    override fun search(expression: String): Flow<Pair<List<Track>, Int>> = flow {
        try {
            val response = networkClient.doRequest(ITunesRequest(expression))
            
            val tracks = if (response.resultCode == COMPLETE_CODE) {
                val favoriteTracksId = appDatabase.trackDao().getFavoriteTracksId().toSet()
                
                (response as ITunesResponse).results.map { trackDto ->
                    val track = TrackDtoMapper.dataToDomainModel(trackDto)
                    
                    val isFavorite = track.trackId != null && track.trackId in favoriteTracksId
                    track.copy(isFavorite = isFavorite)
                }
            } else {
                emptyList()
            }
            
            emit(Pair(tracks, response.resultCode))
        } catch (e: Exception) {
            emit(Pair(emptyList(), FAIL_CODE))
        }
    }
    
    companion object {
        private const val COMPLETE_CODE = 200
        private const val FAIL_CODE = -1
    }
}
