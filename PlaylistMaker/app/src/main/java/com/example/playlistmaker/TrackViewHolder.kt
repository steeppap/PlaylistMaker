package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView = itemView.findViewById(R.id.track_name)
    private val artistNameView: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTimeView: TextView = itemView.findViewById(R.id.track_time)
    private val trackCoverView: ImageView = itemView.findViewById(R.id.track_cover)

    fun bind(model: Track) {
        val trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text = trackTime

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_45)
            .fitCenter()
            .transform(RoundedCorners(2))
            .into(trackCoverView)
    }
}
