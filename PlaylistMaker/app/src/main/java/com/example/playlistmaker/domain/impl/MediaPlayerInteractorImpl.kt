package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerInteractorImpl(
    private val currentTrack: Track?,
    private val playStopBtn: ImageButton,
    private val timeBelowPlayStopBtn: TextView
) : MediaPlayerInteractor {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DEFAULT_TIME = "00:00"
        private const val DELAY = 500L
    }

    private var playerState = STATE_DEFAULT
    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val playbackTime: Runnable = Runnable {
        updatePlaybackTime()
        handler.postDelayed(playbackTime, DELAY)
    }

    override fun preparePlayer() {
        mediaPlayer.setDataSource(currentTrack?.previewUrl)
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

    override fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playStopBtn.setImageResource(R.drawable.ic_stop_btn_100)
        playerState = STATE_PLAYING
        handler.post(playbackTime)
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        handler.removeCallbacks(playbackTime)
        playStopBtn.setImageResource(R.drawable.ic_play_btn_100)
        playerState = STATE_PAUSED
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun removeCallBacks() {
        handler.removeCallbacks(playbackTime)
    }

    private fun updatePlaybackTime() {
        val time =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        timeBelowPlayStopBtn.text = time
    }

}
