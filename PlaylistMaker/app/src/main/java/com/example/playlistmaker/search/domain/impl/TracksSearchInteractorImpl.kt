package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.api.TracksSearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class TracksSearchInteractorImpl(private val repository: TracksRepository) :
    TracksSearchInteractor {
    
    override fun search(expression: String): Flow<Pair<List<Track>, Int>> {
        return repository.search(expression)
    }
}
