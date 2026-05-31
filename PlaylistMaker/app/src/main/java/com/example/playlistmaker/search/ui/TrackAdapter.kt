package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.player.ui.activity.PlayerActivity
import com.example.playlistmaker.search.ui.models.TrackUiModel

class TrackAdapter(
    private var trackList: List<TrackUiModel>,
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
                val intent = Intent(holder.itemView.context, PlayerActivity::class.java).apply {
                    putExtra(EXTRA_TRACK_PREVIEW_URL, trackList[position].previewUrl)
                }
                holder.itemView.context.startActivity(intent)
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
    
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    
    companion object {
        private const val EXTRA_TRACK_PREVIEW_URL = "track_preview_url"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
