package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.api.TracksSearchInteractor
import java.util.concurrent.Executors

class TracksSearchInteractorImpl(private val repository: TracksRepository) :
    TracksSearchInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TracksSearchInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.search(expression))
        }
    }
}
