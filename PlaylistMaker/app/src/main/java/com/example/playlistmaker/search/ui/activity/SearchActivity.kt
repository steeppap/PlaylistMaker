package com.example.playlistmaker.search.ui.activity

import android.os.Bundle
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
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.search.ui.view_model.TrackSearchState

class SearchActivity : AppCompatActivity() {
    private lateinit var bindingSearch: ActivitySearchBinding
    private var inputText: String = ""
    private lateinit var textWatcher: TextWatcher
    private lateinit var trackListAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var viewModel: SearchViewModel
    
    
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
        outState.putString(EDIT_TEXT_KEY, inputText)
    }
    
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedText = savedInstanceState.getString(EDIT_TEXT_KEY)
        bindingSearch.searchEditText.setText(savedText)
    }
    
    private fun initSearchActivity() {
        
        viewModel =
            ViewModelProvider(this, SearchViewModel.getFactory(this))[SearchViewModel::class.java]
        
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
        
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            
            }
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = bindingSearch.searchEditText.text.toString().trim()
                bindingSearch.clearBtn.isVisible = clearButtonVisibility(s)
                viewModel.onTextChanged(inputText)
            }
            
            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
            }
        }
    }
    
    private fun observeHistory() {
        viewModel.observeHistory().observe(this) { trackListHistory ->
            
            if (trackListHistory.isNotEmpty() && inputText.isEmpty()) {
                bindingSearch.apply {
                    historyView.isVisible = true
                    recyclerViewTrackList.isVisible = false
                    connectionError.connectionError.isVisible = false
                    nothingFound.nothingFound.isVisible = false
                }
            } else {
                bindingSearch.apply {
                    historyView.isVisible = false
                }
            }
            historyAdapter.updateTrackList(trackListHistory)
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
                viewModel.searchUpdate(inputText)
            }
        }
    }
    
    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
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
        trackListAdapter.updateTrackList(emptyList())
    }
    
    private fun showHistory() {
        observeHistory()
    }
    
    companion object {
        private const val EDIT_TEXT_KEY = "editTextKey"
    }
}
