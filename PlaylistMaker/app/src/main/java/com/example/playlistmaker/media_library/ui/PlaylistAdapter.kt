package com.example.playlistmaker.media_library.ui

import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.media_library.ui.models.PlaylistUi

class PlaylistAdapter(
    private var playlists: List<PlaylistUi>,
    private val navController: NavController,
    private val onItemClick: ((PlaylistUi) -> Unit)? = null
) : RecyclerView.Adapter<PlaylistViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder.from(parent)
    
    override fun onBindViewHolder(
        holder: PlaylistViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
        
        holder.itemView.setOnClickListener {
            playlists[position].let { playlist ->
                
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
