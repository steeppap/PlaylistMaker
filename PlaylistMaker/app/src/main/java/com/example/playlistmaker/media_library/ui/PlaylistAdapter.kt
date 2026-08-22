package com.example.playlistmaker.media_library.ui

import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media_library.ui.fragments.PlaylistFragment
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
                if (playlist.id != null) {
                    navController.navigate(
                        R.id.playlistFragment,
                        args = PlaylistFragment.createArgs(playlist.id)
                    )
                    onItemClick?.invoke(playlists[position])
                } else {
                   Toast.makeText(holder.itemView.context, "Ошибка данных плейлиста", Toast.LENGTH_SHORT).show()
                }
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
