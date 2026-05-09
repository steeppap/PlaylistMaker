package com.example.playlistmaker.presentation.ui

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var backBtn: ImageButton
    private lateinit var trackCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var playStopBtn: ImageButton
    private lateinit var addToPlaylistBtn: ImageButton
    private lateinit var addToFavoriteBtn: ImageButton
    private lateinit var timeBelowPlayStopBtn: TextView
    private lateinit var trackTime: TextView
    private lateinit var linLayCollection: LinearLayout
    private lateinit var collectionName: TextView
    private lateinit var linLayReleaseDate: LinearLayout
    private lateinit var releaseDate: TextView
    private lateinit var trackGenre: TextView
    private lateinit var country: TextView
    private lateinit var currentTrack: Track
    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private lateinit var handler: Handler
    private lateinit var playbackTime: Runnable

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
        initPlayer()
        setListeners()
        showTrackInfo()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(playbackTime)
        mediaPlayer.release()
    }

    private fun initViews() {
        backBtn = findViewById(R.id.back_button)
        trackCover = findViewById(R.id.track_cover)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        playStopBtn = findViewById(R.id.play_stop_btn)
        addToPlaylistBtn = findViewById(R.id.add_to_playlist)
        addToFavoriteBtn = findViewById(R.id.add_to_favorite)
        timeBelowPlayStopBtn = findViewById(R.id.time_below_play_btn)
        trackTime = findViewById(R.id.track_time)
        linLayCollection = findViewById(R.id.linlay_collection)
        collectionName = findViewById(R.id.collection_name)
        linLayReleaseDate = findViewById(R.id.linlay_releaseDate)
        releaseDate = findViewById(R.id.releaseDate)
        trackGenre = findViewById(R.id.track_genre)
        country = findViewById(R.id.track_country)
    }

    private fun initPlayer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            currentTrack = intent.getParcelableExtra(EXTRA_TRACK, Track::class.java)!!
        }
        handler = Handler(Looper.getMainLooper())
        playbackTime = Runnable {
            updatePlaybackTime()
            handler.postDelayed(playbackTime, DELAY)
        }
        preparePlayer()
    }

    private fun setListeners() {
        backBtn.setOnClickListener { finish() }
        playStopBtn.setOnClickListener {
            playbackControl()
        }
    }

    private fun showTrackInfo() {
        Glide.with(this)
            .load(getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder_312)
            .fitCenter()
            .transform(RoundedCorners(8))
            .into(trackCover)

        val year = currentTrack.releaseDate?.substring(0, 4)

        trackName.text = currentTrack.trackName
        artistName.text = currentTrack.artistName
        trackTime.text = formatMillisToString(currentTrack.trackTimeMillis)
        timeBelowPlayStopBtn.text = DEFAULT_TIME
        collectionName.text = currentTrack.collectionName
        releaseDate.text = year
        trackGenre.text = currentTrack.primaryGenreName
        country.text = currentTrack.country

    }
    private fun preparePlayer() {
        mediaPlayer.setDataSource(currentTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playStopBtn.setImageResource(R.drawable.ic_play_btn_100)
            playerState = STATE_PREPARED
            handler.removeCallbacks(playbackTime)
            timeBelowPlayStopBtn.text = DEFAULT_TIME
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playStopBtn.setImageResource(R.drawable.ic_stop_btn_100)
        playerState = STATE_PLAYING
        handler.post(playbackTime)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        handler.removeCallbacks(playbackTime)
        playStopBtn.setImageResource(R.drawable.ic_play_btn_100)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updatePlaybackTime() {
        val time = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        timeBelowPlayStopBtn.text = time
    }
    private fun getCoverArtwork() =
        currentTrack.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

    private fun formatMillisToString(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val EXTRA_TRACK = "track"
        private const val DEFAULT_TIME = "00:00"
        private const val DELAY = 500L
    }
}
