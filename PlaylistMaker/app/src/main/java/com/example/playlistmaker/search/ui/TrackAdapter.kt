package com.example.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.fragment.PlayerFragment
import com.example.playlistmaker.search.ui.models.TrackUiModel

class TrackAdapter(
    private var trackList: List<TrackUiModel>,
    private val navController: NavController,
    private val onItemClick: ((TrackUiModel) -> Unit)? = null
) : RecyclerView.Adapter<TrackViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder.from(parent)
    
    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        holder.bind(trackList[position])
        
        holder.itemView.setOnClickListener {
            trackList[position].let { track ->
                navController.navigate(
                    R.id.playerFragment,
                    PlayerFragment.createArgs(track)
                )
                onItemClick?.invoke(trackList[position])
            }
        }
    }
    
    override fun getItemCount(): Int {
        return trackList.size
    }
    
    fun updateTrackList(newTrackList: List<TrackUiModel>) {
        this.trackList = newTrackList
        notifyDataSetChanged()
    }
}
