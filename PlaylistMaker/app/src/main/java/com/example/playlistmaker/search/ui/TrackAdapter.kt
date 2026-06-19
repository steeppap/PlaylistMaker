package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
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
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder.from(parent)
    
    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        holder.bind(trackList[position])
        
        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                trackList[position].previewUrl?.let { url ->
                    navController.navigate(
                        R.id.playerFragment,
                        PlayerFragment.createArgs(url)
                    )
                    onItemClick?.invoke(trackList[position])
                }
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
    
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
