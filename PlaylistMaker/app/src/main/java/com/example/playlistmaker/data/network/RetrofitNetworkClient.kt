package com.example.playlistmaker.data.network


import com.example.playlistmaker.Creator
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.ITunesRequest
import com.example.playlistmaker.data.dto.Response


class RetrofitNetworkClient : NetworkClient {
    companion object {
        private const val I_TUNES_BASE_URL = "https://itunes.apple.com"
        private const val BAD_REQUEST_CODE = 400
    }

    val iTunesService = Creator.createITunesService(I_TUNES_BASE_URL)

    override fun doRequest(dto: Any): Response {
        if (dto is ITunesRequest) {
            val response = iTunesService.search(dto.expression).execute()
            val body = response.body() ?: Response()
            return body.apply { resultCode = response.code() }
        } else {
            return Response().apply { resultCode = BAD_REQUEST_CODE }
        }
    }
}
