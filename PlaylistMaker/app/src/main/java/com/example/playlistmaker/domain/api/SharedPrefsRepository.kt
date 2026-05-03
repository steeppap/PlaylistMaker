package com.example.playlistmaker.domain.api

interface SharedPrefsRepository {
    fun saveSetting(key: String, value: Any)
    fun getSetting(key: String): Any?
    fun clearSetting(key: String)
}