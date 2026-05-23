package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.playlistmaker.sharing.domain.api.ResourceProvider

class ResourceProviderImpl(private val context: Context): ResourceProvider {
    override fun getString(resId: Int): String {
        return getString(context, resId)
    }
}
