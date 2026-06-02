package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.creator.SearchDataCreator
import com.example.playlistmaker.search.data.dto.ITunesRequest
import com.example.playlistmaker.search.data.dto.Response

class RetrofitNetworkClient : NetworkClient {

    val iTunesService = SearchDataCreator.createITunesService(I_TUNES_BASE_URL)

    override fun doRequest(dto: Any): Response {
        if (dto is ITunesRequest) {
            val response = iTunesService.search(dto.expression).execute()
            val body = response.body() ?: Response()
            return body.apply { resultCode = response.code() }
        } else {
            return Response().apply { resultCode = BAD_REQUEST_CODE }
        }
    }
    companion object {
        private const val I_TUNES_BASE_URL = "https://itunes.apple.com"
        private const val BAD_REQUEST_CODE = 400
    }
}
