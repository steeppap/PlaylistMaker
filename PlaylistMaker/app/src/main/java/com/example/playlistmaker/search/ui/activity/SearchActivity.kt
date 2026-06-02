package com.example.playlistmaker.search.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.search.ui.view_model.TrackSearchState

class SearchActivity : AppCompatActivity() {
    private lateinit var bindingSearch: ActivitySearchBinding
    private lateinit var textWatcher: TextWatcher
    private lateinit var trackListAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var viewModel: SearchViewModel
    private val handler = Handler(Looper.getMainLooper())
    private var currentSearchRunnable: Runnable? = null
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSearch = ActivitySearchBinding.inflate(layoutInflater)
        
        enableEdgeToEdge()
        setContentView(bindingSearch.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }
        
        initSearchActivity()
        setListeners()
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        currentSearchRunnable?.let { handler.removeCallbacks(it) }
        bindingSearch.searchEditText.removeTextChangedListener(textWatcher)
    }
    
    private fun initSearchActivity() {
        
        viewModel =
            ViewModelProvider(
                this,
                SearchViewModel.getFactory(this)
            )[SearchViewModel::class.java]
        
        trackListAdapter = TrackAdapter(emptyList()) { track ->
            viewModel.addTrackToHistory(track)
        }
        
        historyAdapter =
            TrackAdapter(emptyList()) { track ->
                viewModel.addTrackToHistoryFromHistoryAdapter(track)
            }
        
        bindingSearch.apply {
            recyclerViewHistory.adapter = historyAdapter
            recyclerViewTrackList.adapter = trackListAdapter
        }
        
        viewModel.observeSearchState().observe(this) {
            renderUiState(it)
        }
        
        viewModel.observeTrackList().observe(this) { trackList ->
            trackListAdapter.updateTrackList(trackList)
        }
        
        viewModel.observeClearButtonVisible().observe(this) { visibility ->
            bindingSearch.clearBtn.isVisible = visibility
        }
        
        viewModel.observeSearchQuery().observe(this) { currentText ->
            val currentEditTextText = bindingSearch.searchEditText.text.toString()
            if (currentEditTextText != currentText) {
                bindingSearch.searchEditText.setText(currentText)
            }
            
            if (currentText.isEmpty()) {
                currentSearchRunnable?.let { handler.removeCallbacks(it) }
            } else {
                searchDebounce(currentText, SEARCH_DEBOUNCE_DELAY)
            }
        }
        
        viewModel.observeHistory().observe(this) { trackListHistory ->
            historyAdapter.updateTrackList(trackListHistory)
        }
        
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            
            }
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val newText = s.toString().trim()
                viewModel.updateSearchQuery(newText)
                
            }
            
            override fun afterTextChanged(s: Editable?) {
            
            }
        }
    }
    
    private fun setListeners() {
        bindingSearch.apply {
            backButton.setOnClickListener {
                finish()
            }
            
            clearBtn.setOnClickListener {
                bindingSearch.apply {
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
        (getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.apply {
            hideSoftInputFromWindow(bindingSearch.searchEditText.windowToken, 0)
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
        bindingSearch.apply {
            progressBar.isVisible = false
            recyclerViewTrackList.isVisible = false
            historyView.isVisible = false
            nothingFound.nothingFound.isVisible = false
            connectionError.connectionError.isVisible = false
        }
    }
    
    private fun showLoading() {
        bindingSearch.apply {
            progressBar.isVisible = true
            recyclerViewTrackList.isVisible = false
            historyView.isVisible = false
            nothingFound.nothingFound.isVisible = false
            connectionError.connectionError.isVisible = false
        }
    }
    
    private fun showTracks() {
        bindingSearch.apply {
            recyclerViewTrackList.isVisible = true
            progressBar.isVisible = false
            historyView.isVisible = false
            nothingFound.nothingFound.isVisible = false
            connectionError.connectionError.isVisible = false
        }
    }
    
    private fun showConnectionError() {
        bindingSearch.apply {
            connectionError.connectionError.isVisible = true
            nothingFound.nothingFound.isVisible = false
            progressBar.isVisible = false
            recyclerViewTrackList.isVisible = false
            historyView.isVisible = false
        }
    }
    
    private fun showNothingFoundError() {
        bindingSearch.apply {
            nothingFound.nothingFound.isVisible = true
            connectionError.connectionError.isVisible = false
            progressBar.isVisible = false
            recyclerViewTrackList.isVisible = false
            historyView.isVisible = false
        }
    }
    
    private fun showHistory() {
        bindingSearch.apply {
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
