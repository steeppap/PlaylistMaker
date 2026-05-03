package com.example.playlistmaker.data

import android.util.Log
import com.example.playlistmaker.data.dto.ITunesRequest
import com.example.playlistmaker.data.dto.ITunesResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    companion object {
        private const val COMPLETE_CODE = 200
        private const val FAIL_CODE = -1
    }

    override fun search(expression: String): Pair<List<Track>, Int> {
        return try {
            val response = networkClient.doRequest(ITunesRequest(expression))
            val tracks = if (response.resultCode == COMPLETE_CODE) {
                (response as ITunesResponse).results.map { track ->
                    Track(
                        trackName = track.trackName,
                        artistName = track.artistName,
                        trackTime = formatMillisToString(track.trackTimeMillis),
                        artworkUrl100 = track.artworkUrl100,
                        trackId = track.trackId,
                        collectionName = track.collectionName,
                        releaseDate = track.releaseDate,
                        primaryGenreName = track.primaryGenreName,
                        country = track.country,
                        previewUrl = track.previewUrl
                    )
                }
            } else {
                emptyList()
            }

            Pair(tracks, response.resultCode)
        } catch (e: Exception) {
            Pair(emptyList(), FAIL_CODE)
        }
    }


    private fun formatMillisToString(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
