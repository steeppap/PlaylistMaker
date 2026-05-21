package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksSearchInteractor {
    fun search(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: Pair<List<Track>, Int>)
    }
}
