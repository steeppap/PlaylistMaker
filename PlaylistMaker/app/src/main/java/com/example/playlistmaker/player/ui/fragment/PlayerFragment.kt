package com.example.playlistmaker.player.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.ui.PlaylistInPlayerAdapter
import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.models.TrackUiModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlayerViewModel
    private lateinit var playlistAdapter: PlaylistInPlayerAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    
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
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            this.state = BottomSheetBehavior.STATE_HIDDEN
        }
        initPlayerFragment()
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
    
    override fun onResume() {
        super.onResume()
        updateOverlayState()
    }
    
    private fun initPlayerFragment() {
        playlistAdapter =
            PlaylistInPlayerAdapter(emptyList()) { playlist ->
                viewModel.addTrackToPlaylist(playlist)
            }
        
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(ARGS_TRACK, TrackUiModel::class.java)
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
        
        viewModel = getViewModel { parametersOf(track) }
        
        viewModel.observePlayerStateWithProgress().observe(viewLifecycleOwner) {
            binding.timeBelowPlayBtn.text = it.progressTime
            changeButtonIcon(it.playerState == PlayerViewModel.STATE_PLAYING)
        }
        viewModel.observeTrackUiModel().observe(viewLifecycleOwner) {
            showTrackInfo(it)
        }
        viewModel.observeFavoriteTrack().observe(viewLifecycleOwner) { isFavorite ->
            favoriteState(isFavorite)
        }
        viewModel.observePlaylistsState().observe(viewLifecycleOwner) { playlists ->
            playlistAdapter.updatePlaylists(playlists)
        }
        viewModel.observeInPlaylistState().observe(viewLifecycleOwner) { event ->
            
            if (event?.shouldHideBottomSheet == null) {
                return@observe
            } else if (event.shouldHideBottomSheet) {
                Toast.makeText(
                    requireContext(),
                    getString(
                        R.string.added_to_playlist,
                        event.message
                    ),
                    Toast.LENGTH_LONG
                ).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(
                        R.string.track_has_already_been_added_to_playlist,
                        event.message
                    ),
                    Toast.LENGTH_LONG
                ).show()
            }
            viewModel.clearInPlaylistEvent()
            
        }
        binding.recyclerViewPlaylists.adapter = playlistAdapter
        
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }
            
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = (slideOffset + 1f) / 2
                binding.overlay.alpha = alpha
            }
        })
    }
    
    private fun setListeners() {
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        
        binding.playStopBtn.setOnClickListener {
            viewModel.playbackControl()
        }
        
        binding.addToFavorite.setOnClickListener {
            lifecycleScope.launch {
                viewModel.toggleFavorite()
            }
        }
        
        binding.addToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.newPlaylist.setOnClickListener { findNavController().navigate(R.id.createPlaylistFragment) }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                findNavController().navigateUp()
            }
        }
    }
    
    private fun updateOverlayState() {
        val state = bottomSheetBehavior.state
        
        when (state) {
            BottomSheetBehavior.STATE_HIDDEN -> {
                binding.overlay.visibility = View.GONE
                binding.overlay.alpha = 0f
            }
            
            else -> {
                binding.overlay.visibility = View.VISIBLE
                binding.overlay.alpha = 0.6f
            }
        }
    }
    
    private fun favoriteState(isFavorite: Boolean) {
        binding.addToFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_active_51
            else R.drawable.ic_favorite_non_active_51
        )
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
        private const val ARGS_TRACK = "track"
        
        fun createArgs(track: TrackUiModel): Bundle =
            bundleOf(ARGS_TRACK to track)
    }
}
