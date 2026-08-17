package com.example.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.media_library.ui.models.PlaylistUi

class PlaylistInPlayerAdapter(
    private var playlists: List<PlaylistUi>,
    private val onItemClick: ((PlaylistUi) -> Unit)? = null
) : RecyclerView.Adapter<PlaylistInPlayerViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistInPlayerViewHolder =
        PlaylistInPlayerViewHolder.from(parent)
    
    override fun onBindViewHolder(
        holder: PlaylistInPlayerViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
        
        holder.itemView.setOnClickListener {
            playlists[position].let {
                onItemClick?.invoke(playlists[position])
            }
        }
    }
    
    override fun getItemCount(): Int {
        return playlists.size
    }
    
    fun updatePlaylists(newPlaylists: List<PlaylistUi>) {
        this.playlists = newPlaylists
        notifyDataSetChanged()
    }
}
