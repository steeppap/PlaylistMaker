package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackCardBinding
import com.example.playlistmaker.search.domain.models.Track

class TrackViewHolder(private val binding: TrackCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {
        binding.apply {
            trackTime.text = formatMillisToString(model.trackTimeMillis)
            trackName.text = model.trackName
            artistName.text = model.artistName
        }

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_45)
            .fitCenter()
            .transform(RoundedCorners(2))
            .into(binding.trackCover)
    }

    private fun formatMillisToString(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

    companion object {
        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackCardBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }
}
