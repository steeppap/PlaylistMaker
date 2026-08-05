package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    
    override suspend fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }
    
    override suspend fun getTracksHistory(): List<Track> {
        return repository.getTracksHistory()
    }
    
    override fun clearTracksHistory() {
        repository.clearTracksHistory()
    }
    
    override suspend fun getTrackByPreviewUrl(trackPreviewUrl: String?): Track? {
        return repository.getTrackByPreviewUrl(trackPreviewUrl)
    }
    
}
