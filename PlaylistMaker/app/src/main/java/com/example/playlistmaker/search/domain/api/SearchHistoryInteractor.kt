package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchHistoryInteractor {
    suspend fun addTrackToHistory(track: Track)
    suspend fun getTracksHistory(): List<Track>
    fun clearTracksHistory()
    suspend fun getTrackByPreviewUrl(trackPreviewUrl: String?): Track?
}
