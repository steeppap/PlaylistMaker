package com.example.playlistmaker.media_library.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.media_library.ui.viewmodels.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment: Fragment() {
    private lateinit var binding: FragmentFavoriteTracksBinding
    private val viewModel: FavoriteTracksViewModel by viewModel()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }
    
    
    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}
