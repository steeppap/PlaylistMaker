package com.example.playlistmaker.player.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.models.TrackUiModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlayerViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlayerActivity()
        setListeners()
    }
    
    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun initPlayerActivity() {
        val trackPreviewUrl = requireArguments().getString(ARGS_TRACK_PREVIEW_URL) ?: ""
        
        viewModel = getViewModel { parametersOf(trackPreviewUrl) }
        
        viewModel.observePlayerStateWithProgress().observe(viewLifecycleOwner) {
            binding.timeBelowPlayBtn.text = it.progressTime
            changeButtonIcon(it.playerState == PlayerViewModel.STATE_PLAYING)
        }
        viewModel.observeTrackUiModel().observe(viewLifecycleOwner) {
            showTrackInfo(it)
        }
    }
    
    private fun setListeners() {
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        
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
        private const val ARGS_TRACK_PREVIEW_URL = "track_preview_url"
        
        fun createArgs(trackPreviewUrl: String): Bundle =
            bundleOf(ARGS_TRACK_PREVIEW_URL to trackPreviewUrl)
    }
}
