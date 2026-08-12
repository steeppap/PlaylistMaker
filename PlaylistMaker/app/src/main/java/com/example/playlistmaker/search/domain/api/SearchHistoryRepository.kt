package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    suspend fun addTrackToHistory(track: Track)
    suspend fun getTracksHistory(): List<Track>
    fun clearTracksHistory()
    suspend fun getTrackByPreviewUrl(trackPreviewUrl: String?): Track?
}
