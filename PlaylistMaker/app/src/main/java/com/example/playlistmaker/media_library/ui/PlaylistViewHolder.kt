package com.example.playlistmaker.media_library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistInMediaLibraryBinding
import com.example.playlistmaker.media_library.ui.models.PlaylistUi

class PlaylistViewHolder(private val binding: ItemPlaylistInMediaLibraryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    
    fun bind(view: PlaylistUi) {
        binding.apply {
            playlistTitle.text = view.title
            
            trackCounts.text = itemView.context.resources.getQuantityString(
                R.plurals.track_count,
                view.tracksCount,
                view.tracksCount
            )
        }
        
        Glide.with(itemView)
            .load(view.coverPath)
            .placeholder(R.drawable.ic_placeholder_104)
            .fitCenter()
            .into(binding.playlistCover)
    }
    
    companion object {
        fun from(parent: ViewGroup): PlaylistViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaylistInMediaLibraryBinding.inflate(inflater, parent, false)
            return PlaylistViewHolder(binding)
        }
    }
}

