package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.ITunesRequest
import com.example.playlistmaker.data.dto.ITunesResponse
import com.example.playlistmaker.data.extension.TrackMapper
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

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
