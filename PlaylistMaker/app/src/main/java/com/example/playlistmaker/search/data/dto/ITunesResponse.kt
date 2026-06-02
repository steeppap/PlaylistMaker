package com.example.playlistmaker.search.data.dto

class ITunesResponse (
    val resultCount: Int,
    val results: List<TrackDto>
    ): Response()