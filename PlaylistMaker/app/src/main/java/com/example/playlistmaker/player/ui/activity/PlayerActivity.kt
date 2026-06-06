package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.models.TrackUiModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf


class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var viewModel: PlayerViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        initPlayerActivity()
        setListeners()
    }
    
    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
    
    private fun initPlayerActivity() {
        viewModel = getViewModel {
            parametersOf(
                intent.getStringExtra(EXTRA_TRACK_PREVIEW_URL)
            )
        }
        viewModel.observePlayerStateWithProgress().observe(this) {
            binding.timeBelowPlayBtn.text = it.progressTime
            changeButtonIcon(it.playerState == PlayerViewModel.STATE_PLAYING)
        }
        viewModel.observeTrackUiModel().observe(this) {
            showTrackInfo(it)
        }
    }
    
    private fun setListeners() {
        binding.backButton.setOnClickListener { finish() }
        
        binding.playStopBtn.setOnClickListener {
            viewModel.playbackControl()
        }
    }
    
    private fun changeButtonIcon(isPlaying: Boolean) {
        binding.playStopBtn.setImageResource(if (isPlaying) R.drawable.ic_stop_btn_100 else R.drawable.ic_play_btn_100)
    }
    
    private fun showTrackInfo(currentTrack: TrackUiModel) {
        Glide.with(this)
            .load(getCoverArtwork(currentTrack))
            .placeholder(R.drawable.ic_placeholder_312)
            .fitCenter()
            .transform(RoundedCorners(8))
            .into(binding.trackCover)
        
        val year = currentTrack.releaseDate?.substring(0, 4)
        
        binding.apply {
            trackName.text = currentTrack.trackName
            artistName.text = currentTrack.artistName
            trackTime.text = formatMillisToString(currentTrack.trackTimeMillis)
            collectionName.text = currentTrack.collectionName
            releaseDate.text = year
            trackGenre.text = currentTrack.primaryGenreName
            trackCountry.text = currentTrack.country
        }
    }
    
    private fun getCoverArtwork(track: TrackUiModel) =
        track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
    
    private fun formatMillisToString(millis: Int): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
    
    companion object {
        private const val EXTRA_TRACK_PREVIEW_URL = "track_preview_url"
    }
}
