package com.example.playlistmaker.search.ui.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.search.ui.view_model.TrackSearchState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var textWatcher: TextWatcher
    private lateinit var trackListAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private val viewModel: SearchViewModel by viewModel()
    private val handler = Handler(Looper.getMainLooper())
    private var currentSearchRunnable: Runnable? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchActivity()
        setListeners()
    }
    
    override fun onDestroyView() {
        binding.searchEditText.removeTextChangedListener(textWatcher)
        trackListAdapter.updateTrackList(emptyList())
        historyAdapter.updateTrackList(emptyList())
        currentSearchRunnable?.let { handler.removeCallbacks(it) }
        
        super.onDestroyView()
        _binding = null
    }
    
    private fun initSearchActivity() {
        
        trackListAdapter = TrackAdapter(emptyList(), findNavController()) { track ->
            viewModel.addTrackToHistory(track)
        }
        
        historyAdapter =
            TrackAdapter(emptyList(), findNavController()) { track ->
                viewModel.addTrackToHistoryFromHistoryAdapter(track)
            }
        
        binding.apply {
            recyclerViewHistory.adapter = historyAdapter
            recyclerViewTrackList.adapter = trackListAdapter
        }
        
        viewModel.observeSearchState().observe(viewLifecycleOwner) {
            renderUiState(it)
        }
        
        viewModel.observeTrackList().observe(viewLifecycleOwner) { trackList ->
            trackListAdapter.updateTrackList(trackList)
        }
        
        viewModel.observeClearButtonVisible().observe(viewLifecycleOwner) { visibility ->
            binding.clearBtn.isVisible = visibility
        }
        
        viewModel.observeSearchQuery().observe(viewLifecycleOwner) { currentText ->
            val currentEditTextText = binding.searchEditText.text.toString()
            if (currentEditTextText != currentText) {
                binding.searchEditText.setText(currentText)
            }
            
            if (currentText.isEmpty()) {
                currentSearchRunnable?.let { handler.removeCallbacks(it) }
            } else {
                searchDebounce(currentText, SEARCH_DEBOUNCE_DELAY)
            }
        }
        
        viewModel.observeHistory().observe(viewLifecycleOwner) { trackListHistory ->
            historyAdapter.updateTrackList(trackListHistory)
        }
        
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            
            }
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newText = s.toString()
                viewModel.updateSearchQuery(newText)
                
            }
            
            override fun afterTextChanged(s: Editable?) {
            
            }
        }
    }
    
    private fun setListeners() {
        binding.apply {
            
            clearBtn.setOnClickListener {
                binding.apply {
                    searchEditText.setText("")
                    searchEditText.clearFocus()
                }
                hideKeyboard()
            }
            
            clearHistoryBtn.setOnClickListener {
                viewModel.clearTracksHistory()
            }
            
            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                viewModel.onSearchFocused()
            }
            
            searchEditText.addTextChangedListener(textWatcher)
            
            connectionError.updateBtn.setOnClickListener {
                val currentQuery = viewModel.observeSearchQuery().value.toString()
                viewModel.search(currentQuery)
            }
        }
    }
    
    private fun searchDebounce(query: String, debounceDelay: Long) {
        currentSearchRunnable?.let {
            handler.removeCallbacks(it)
        }
        
        currentSearchRunnable = Runnable {
            viewModel.search(query)
        }
        handler.postDelayed(currentSearchRunnable!!, debounceDelay)
    }
    
    private fun hideKeyboard() {
        (requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
            hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }
    }
    
    private fun renderUiState(state: TrackSearchState) {
        when (state) {
            TrackSearchState.Default -> showDefaultUiState()
            TrackSearchState.Loading -> showLoading()
            TrackSearchState.Success -> showTracks()
            TrackSearchState.Empty -> showNothingFoundError()
            TrackSearchState.Error -> showConnectionError()
            TrackSearchState.History -> showHistory()
        }
    }
    
    private fun showDefaultUiState() {
        binding.apply {
            progressBar.isVisible = false
            recyclerViewTrackList.isVisible = false
            historyView.isVisible = false
            nothingFound.nothingFound.isVisible = false
            connectionError.connectionError.isVisible = false
        }
    }
    
    private fun showLoading() {
        binding.apply {
            progressBar.isVisible = true
            recyclerViewTrackList.isVisible = false
            historyView.isVisible = false
            nothingFound.nothingFound.isVisible = false
            connectionError.connectionError.isVisible = false
        }
    }
    
    private fun showTracks() {
        binding.apply {
            recyclerViewTrackList.isVisible = true
            progressBar.isVisible = false
            historyView.isVisible = false
            nothingFound.nothingFound.isVisible = false
            connectionError.connectionError.isVisible = false
        }
    }
    
    private fun showConnectionError() {
        binding.apply {
            connectionError.connectionError.isVisible = true
            nothingFound.nothingFound.isVisible = false
            progressBar.isVisible = false
            recyclerViewTrackList.isVisible = false
            historyView.isVisible = false
        }
    }
    
    private fun showNothingFoundError() {
        binding.apply {
            nothingFound.nothingFound.isVisible = true
            connectionError.connectionError.isVisible = false
            progressBar.isVisible = false
            recyclerViewTrackList.isVisible = false
            historyView.isVisible = false
        }
    }
    
    private fun showHistory() {
        binding.apply {
            historyView.isVisible = true
            recyclerViewTrackList.isVisible = false
            connectionError.connectionError.isVisible = false
            nothingFound.nothingFound.isVisible = false
        }
    }
    
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1500L
    }
}
