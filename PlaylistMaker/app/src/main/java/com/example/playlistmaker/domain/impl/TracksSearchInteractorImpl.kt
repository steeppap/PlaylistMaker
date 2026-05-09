package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksSearchInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksSearchInteractorImpl(private val repository: TracksRepository) : TracksSearchInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun search(expression: String, consumer: TracksSearchInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.search(expression))
        }
    }
}
