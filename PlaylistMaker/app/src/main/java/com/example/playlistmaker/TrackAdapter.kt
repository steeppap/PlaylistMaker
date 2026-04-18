package com.example.playlistmaker

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val trackList: MutableList<Track>,
    private val onItemClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackViewHolder>() {

    companion object {
        private const val EXTRA_TRACK = "track"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.track_card, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        holder.bind(trackList[position])

        holder.itemView.setOnClickListener {
            if (clickDebounce()) {
                val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
                intent.putExtra(EXTRA_TRACK, trackList[position])
                holder.itemView.context.startActivity(intent)
                onItemClick?.invoke(trackList[position])
            }
        }

    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun updateTrackList(newTrackList: List<Track>) {
        trackList.clear()
        trackList.addAll(newTrackList)
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
}
