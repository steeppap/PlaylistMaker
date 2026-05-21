package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.ITunesRequest
import com.example.playlistmaker.search.data.dto.ITunesResponse
import com.example.playlistmaker.search.data.extension.TrackMapper
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun search(expression: String): Pair<List<Track>, Int> {
        return try {
            val response = networkClient.doRequest(ITunesRequest(expression))
            val tracks = if (response.resultCode == COMPLETE_CODE) {
                (response as ITunesResponse).results.map { track ->
                    TrackMapper.dataToDomainModel(track)
                }
            } else {
                emptyList()
            }

            Pair(tracks, response.resultCode)
        } catch (e: Exception) {
            Pair(emptyList(), FAIL_CODE)
        }
    }

    companion object {
        private const val COMPLETE_CODE = 200
        private const val FAIL_CODE = -1
    }
}