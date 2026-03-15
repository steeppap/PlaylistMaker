package com.example.playlistmaker

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val trackList: MutableList<Track>,
    private val onItemClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackViewHolder>() {


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
            val intent = Intent(holder.itemView.context, PlayerActivity::class.java)
            intent.putExtra("track", trackList[position])
            holder.itemView.context.startActivity(intent)
            onItemClick?.invoke(trackList[position])
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
}
