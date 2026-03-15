package com.example.playlistmaker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageButton
    private lateinit var trackCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var playBtn: ImageButton
    private lateinit var addToPlaylistBtn: ImageButton
    private lateinit var addToFavoriteBtn: ImageButton
    private lateinit var timeBelowPlayBtn: TextView
    private lateinit var trackTime: TextView
    private lateinit var linLayCollection: LinearLayout
    private lateinit var collectionName: TextView
    private lateinit var linLayReleaseDate: LinearLayout
    private lateinit var releaseDate: TextView
    private lateinit var trackGenre: TextView
    private lateinit var country: TextView
    private var currentTrack: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        setListeners()
        showTrackInfo()
    }

    private fun initViews() {
        backBtn = findViewById(R.id.back_button)
        trackCover = findViewById(R.id.track_cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        playBtn = findViewById(R.id.play_stop_btn)
        addToPlaylistBtn = findViewById(R.id.add_to_playlist)
        addToFavoriteBtn = findViewById(R.id.add_to_favorite)
        timeBelowPlayBtn = findViewById(R.id.time_below_play_btn)
        trackTime = findViewById(R.id.track_time)
        linLayCollection = findViewById(R.id.linlay_collection)
        collectionName = findViewById(R.id.collection_name)
        linLayReleaseDate = findViewById(R.id.linlay_releaseDate)
        releaseDate = findViewById(R.id.releaseDate)
        trackGenre = findViewById(R.id.track_genre)
        country = findViewById(R.id.track_country)

        currentTrack = intent.getParcelableExtra("track")
    }

    private fun setListeners() {
        backBtn.setOnClickListener { finish() }

    }

    private fun showTrackInfo() {
        Glide.with(this)
            .load(getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_312)
            .fitCenter()
            .transform(RoundedCorners(8))
            .into(trackCover)

        val currentTrackTime = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(currentTrack?.trackTimeMillis)
        val year = currentTrack?.releaseDate?.substring(0, 4)
        trackName.text = currentTrack?.trackName
        artistName.text = currentTrack?.artistName
        trackTime.text = currentTrackTime
        timeBelowPlayBtn.text = currentTrackTime
        collectionName.text = currentTrack?.collectionName
        releaseDate.text = year
        trackGenre.text = currentTrack?.primaryGenreName
        country.text = currentTrack?.country

    }

    private fun getCoverArtwork() =
        currentTrack?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
}
