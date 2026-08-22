package com.example.playlistmaker.media_library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media_library.ui.PlaylistAdapter
import com.example.playlistmaker.media_library.ui.states.PlaylistState
import com.example.playlistmaker.media_library.ui.viewmodels.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlaylistsViewModel by viewModel()
    private lateinit var playlistAdapter: PlaylistAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
        setListeners()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun initFragment() {
        playlistAdapter =
            PlaylistAdapter(emptyList(), findNavController()) {}
        
        binding.recyclerViewPlaylists.layoutManager =
            GridLayoutManager(requireContext(), 2)
        binding.recyclerViewPlaylists.adapter = playlistAdapter
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderUiState(state)
        }
    }
    
    private fun setListeners() {
        binding.newPlaylist.setOnClickListener {
            findNavController().navigate(R.id.createPlaylistFragment)
        }
    }
    
    private fun renderUiState(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> binding.apply {
                emptyPlaylists.emptyPlaylists.isVisible = true
                recyclerViewPlaylists.isVisible = false
            }
            
            is PlaylistState.Content -> {
                playlistAdapter.updatePlaylists(state.playlists)
                binding.apply {
                    emptyPlaylists.emptyPlaylists.isVisible = false
                    recyclerViewPlaylists.isVisible = true
                }
            }
        }
    }
    
    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
