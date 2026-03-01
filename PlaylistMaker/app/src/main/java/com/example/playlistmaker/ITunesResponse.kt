package com.example.playlistmaker

import com.google.gson.annotations.SerializedName

class ITunesResponse (
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<Track>
    )