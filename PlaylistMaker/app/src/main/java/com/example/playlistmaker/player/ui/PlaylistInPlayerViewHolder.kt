package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistInPlayerBinding
import com.example.playlistmaker.media_library.ui.models.PlaylistUi

class PlaylistInPlayerViewHolder(private val binding: ItemPlaylistInPlayerBinding) :
    RecyclerView.ViewHolder(binding.root) {
    
    fun bind(view: PlaylistUi) {
        binding.apply {
            playlistTitle.text = view.title
            
            val count = view.tracksCount
            trackCounts.text = when {
                count % 10 == 1 -> "$count трек"
                count % 10 in 2..4 -> "$count трека"
                count % 100 in 11..19 -> "$count треков"
                else -> "$count треков"
            }
        }
        
        Glide.with(itemView)
            .load(view.coverPath)
            .placeholder(R.drawable.ic_placeholder_45)
            .fitCenter()
            .into(binding.playlistCover)
    }
    
    companion object {
        fun from(parent: ViewGroup): PlaylistInPlayerViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaylistInPlayerBinding.inflate(inflater, parent, false)
            return PlaylistInPlayerViewHolder(binding)
        }
    }
}

