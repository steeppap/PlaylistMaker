package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.ITunesRequest
import com.example.playlistmaker.search.data.dto.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(private val iTunesService: ITunesApiService) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (dto !is ITunesRequest) {
            return Response().apply { resultCode = BAD_REQUEST_CODE }
            
        }
        return withContext(Dispatchers.IO){
            try{
                val response = iTunesService.search(dto.expression)
                response.apply { resultCode = COMPLETE_CODE }
            }
            catch (e: Throwable){
                Response().apply { resultCode = FAIL_CODE }
            }
        }
    }
    companion object {
        private const val BAD_REQUEST_CODE = 400
        private const val COMPLETE_CODE = 200
        private const val FAIL_CODE = -1
    }
}
