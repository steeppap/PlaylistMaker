package com.example.playlistmaker.media_library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.media_library.ui.viewmodels.FavoriteState
import com.example.playlistmaker.media_library.ui.viewmodels.FavoriteTracksViewModel
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {
    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteTracksViewModel by viewModel()
    private lateinit var trackAdapter: TrackAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFavoriteTracksFragment()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        trackAdapter.updateTrackList(emptyList())
    }
    
    private fun initFavoriteTracksFragment() {
        viewModel.observeStateLiveData().observe(viewLifecycleOwner) { state ->
            when(state){
                is FavoriteState.Empty -> {
                    binding.emptyFavoriteTracks.emptyFavoriteTracks.isVisible = true
                    binding.recyclerView.isVisible = false
                }
                is FavoriteState.WithTracks -> {
                    binding.recyclerView.isVisible = true
                    binding.emptyFavoriteTracks.emptyFavoriteTracks.isVisible = false
                    trackAdapter.updateTrackList(state.tracks)
                }
            }
        }
        trackAdapter = TrackAdapter(emptyList(), findNavController()) {trackUiModel ->
            trackUiModel.isFavorite = true
        }
        binding.recyclerView.adapter = trackAdapter
    }
    
    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}
