package com.example.playlistmaker.data.dto

class ITunesResponse (
    val resultCount: Int,
    val results: List<TrackDto>
    ): Response()