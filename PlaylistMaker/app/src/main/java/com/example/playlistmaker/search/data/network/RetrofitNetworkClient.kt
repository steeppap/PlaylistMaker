package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.ITunesRequest
import com.example.playlistmaker.search.data.dto.Response

class RetrofitNetworkClient(private val iTunesService: ITunesApiService) : NetworkClient {

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
        private const val BAD_REQUEST_CODE = 400
    }
}
