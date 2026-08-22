package com.example.playlistmaker.media_library.ui.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media_library.ui.models.PlaylistUi
import com.example.playlistmaker.media_library.ui.viewmodels.CreatePlaylistViewModel
import com.example.playlistmaker.media_library.ui.viewmodels.PlaylistViewModel
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.models.TrackUiModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PlaylistViewModel
    private lateinit var trackListAdapter: TrackAdapter
    private lateinit var bottomSheetTracksBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetMoreBehavior: BottomSheetBehavior<ConstraintLayout>
    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null
    private lateinit var currentTracks: List<TrackUiModel>
    private lateinit var currentPlaylist: PlaylistUi
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        bottomSheetTracksBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet)
        initFragment()
        setListeners()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        if (bottomSheetCallback != null) {
            bottomSheetMoreBehavior.removeBottomSheetCallback(bottomSheetCallback!!)
            bottomSheetCallback = null
        }
        trackListAdapter.updateTrackList(emptyList())
        _binding = null
    }
    
    override fun onResume() {
        super.onResume()
        bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
    
    private fun initFragment() {
        val playlistId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getLong(ARGS_PLAYLIST_ID)
            
        } else {
            TODO("VERSION.SDK_INT < TIRAMISU")
        }
        viewModel = getViewModel { parametersOf(playlistId) }
        trackListAdapter = TrackAdapter(
            emptyList(),
            findNavController(),
            {},
            { track ->
                showDeleteTrackDialog(track)
            })
        binding.recyclerViewTracks.adapter = trackListAdapter
        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            currentPlaylist = playlist
            binding.apply {
                playlistTitle.text = playlist.title
                if (playlist.description.isNullOrBlank()) {
                    playlistDescription.visibility = View.GONE
                } else {
                    playlistDescription.visibility = View.VISIBLE
                    playlistDescription.text = playlist.description
                }
                
                tracksCount.text = tracksCount.context.resources.getQuantityString(
                    R.plurals.track_count,
                    playlist.tracksCount,
                    playlist.tracksCount
                )
            }
            Glide.with(binding.playlistCover)
                .load(playlist.coverPath)
                .placeholder(R.drawable.ic_placeholder_312)
                .into(binding.playlistCover)
        }
        viewModel.observePlaylistDuration().observe(viewLifecycleOwner) { duration ->
            val durationFormat: Int =
                SimpleDateFormat("mm", Locale.getDefault()).format(duration).toInt()
            binding.playlistDuration.text =
                binding.playlistDuration.context.resources.getQuantityString(
                    R.plurals.track_duration,
                    durationFormat,
                    durationFormat
                )
        }
        viewModel.observeTracksPlaylistUi().observe(viewLifecycleOwner) { trackList ->
            if (trackList.isNullOrEmpty()) {
                binding.noTracks.isVisible = true
                binding.recyclerViewTracks.isVisible = false
                currentTracks = emptyList()
            } else {
                binding.noTracks.isVisible = false
                binding.recyclerViewTracks.isVisible = true
                currentTracks = trackList
            }
            trackListAdapter.updateTrackList(trackList)
        }
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                calculateAndSetPeekHeight()
            }
        })
        bottomSheetMoreBehavior = BottomSheetBehavior.from(binding.moreBottomSheet).apply {
            this.state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetCallback = object :
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
        }
        bottomSheetMoreBehavior.addBottomSheetCallback(bottomSheetCallback!!)
    }
    
    private fun setListeners() {
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { findNavController().navigateUp() }
        binding.share.setOnClickListener { sharePlaylist(currentPlaylist) }
        binding.more.setOnClickListener {
            openBottomSheetMore()
        }
    }
    
    private fun calculateAndSetPeekHeight() {
        val parentHeight = binding.root.height
        val viewBottom = binding.upperConstraint.bottom
        val marginBottom = binding.upperConstraint.marginBottom
        
        val freeSpace =
            if (parentHeight > viewBottom) parentHeight - (viewBottom + marginBottom) else 0
        
        bottomSheetTracksBehavior.peekHeight = freeSpace
    }
    
    private fun showDeleteTrackDialog(track: TrackUiModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.wanna_delete_track)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.removeTrackFromPlaylist(track.trackId!!)
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_playlist)
            .setMessage(getString(R.string.wanna_delete_playlist, binding.playlistTitle.text))
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.removePlaylistById()
                findNavController().navigateUp()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun sharePlaylist(playlist: PlaylistUi) {
        bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        if (currentTracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_share_empty),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        
        val shareText = buildString {
            append("Плейлист: " + playlist.title)
            
            if (!playlist.description.isNullOrBlank()) {
                append("\n")
                append("Описание: " + playlist.description)
            }
            
            append("\n")
            append(
                context?.resources?.getQuantityString(
                    R.plurals.track_count,
                    currentTracks.size,
                    currentTracks.size
                )
            )
            append("\n\n")
            
            currentTracks.forEachIndexed { index, track ->
                val duration =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
                append(
                    "${index + 1}. " + "${track.artistName} - " + "${track.trackName} " + "($duration)"
                )
                
                if (index != currentTracks.lastIndex) {
                    append("\n")
                }
            }
        }
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        
        startActivity(Intent.createChooser(shareIntent, null))
    }
    
    private fun openBottomSheetMore() {
        bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.apply {
            Glide.with(playlistCoverInSheet)
                .load(currentPlaylist.coverPath)
                .placeholder(R.drawable.ic_placeholder_45)
                .fitCenter()
                .into(playlistCoverInSheet)
            playlistTitleInSheet.text = currentPlaylist.title
            trackCountsSheet.text = context?.resources?.getQuantityString(
                R.plurals.track_count,
                currentTracks.size,
                currentTracks.size
            )
            
            shareBtn.setOnClickListener {
                sharePlaylist(currentPlaylist)
            }
            
            deletePlaylistBtn.setOnClickListener {
                bottomSheetMoreBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                showDeletePlaylistDialog()
            }
            
            editInfoBtn.setOnClickListener {
                findNavController().navigate(
                    R.id.createPlaylistFragment,
                    CreatePlaylistFragment.createArgs(currentPlaylist.id!!)
                )
            }
        }
    }
    
    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist"
        
        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }
}
