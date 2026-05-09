package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksSearchInteractor {
    fun search(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: Pair<List<Track>, Int>)
    }
}
