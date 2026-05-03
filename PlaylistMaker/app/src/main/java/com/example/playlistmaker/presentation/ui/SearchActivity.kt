package com.example.playlistmaker.presentation.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.TracksSearchInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.TrackAdapter
import com.example.playlistmaker.presentation.TrackListUpdateConsumer


class SearchActivity : AppCompatActivity() {
    companion object {
        private const val EDIT_TEXT_KEY = "editTextKey"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private lateinit var editText: EditText
    private lateinit var backButton: Button
    private lateinit var updateButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var clearButton: ImageView
    private lateinit var connectionError: LinearLayout
    private lateinit var nothingFoundError: LinearLayout
    private var inputText: String = ""
    private lateinit var textWatcher: TextWatcher
    private var trackList = mutableListOf<Track>()
    private lateinit var recyclerTrackList: RecyclerView
    private lateinit var recyclerHistory: RecyclerView
    private lateinit var historyView: View
    private lateinit var trackListAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private var lastSearchQuery: String = ""
    private lateinit var progressBar: ProgressBar
    private lateinit var handler: Handler
    private lateinit var searchRunnable: Runnable
    private lateinit var tracksSearchInteractor: TracksSearchInteractor
    private lateinit var tracksConsumer: TracksSearchInteractor.TracksConsumer
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }

        initViews()
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
        editText.setText(savedText)
    }

    private fun initViews() {
        editText = findViewById(R.id.search_edit_text)
        backButton = findViewById(R.id.back_button)
        updateButton = findViewById(R.id.update_btn)
        clearButton = findViewById(R.id.clear_icon)
        clearHistoryButton = findViewById(R.id.clear_history_btn)
        recyclerTrackList = findViewById(R.id.recyclerViewTrackList)
        recyclerHistory = findViewById(R.id.recyclerViewHistory)
        historyView = findViewById(R.id.history_view)
        connectionError = findViewById(R.id.connection_error)
        nothingFoundError = findViewById(R.id.nothing_found)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setListeners() {
        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            trackList.clear()
            trackListAdapter.notifyDataSetChanged()
            editText.setText("")
            editText.clearFocus()
            hideKeyboard()
        }

        clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearTracksHistory()
            historyAdapter.updateTrackList(searchHistoryInteractor.getTracksHistory())
            historyView.visibility = View.GONE
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHistory()
            }
        }

        editText.addTextChangedListener(textWatcher)

        updateButton.setOnClickListener {
            update(lastSearchQuery)
        }
    }

    private fun initSearchActivity() {
        tracksSearchInteractor = Creator.provideTracksSearchInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor(this)
        handler = Handler(Looper.getMainLooper())
        searchRunnable = Runnable { search(lastSearchQuery) }

        trackListAdapter = TrackAdapter(trackList) { track ->
            searchHistoryInteractor.addTrackToHistory(track)
        }


        historyAdapter =
            TrackAdapter(searchHistoryInteractor.getTracksHistory().toMutableList()) { track ->
                searchHistoryInteractor.addTrackToHistory(track)
                showHistory()
            }

        recyclerTrackList.adapter = trackListAdapter
        recyclerHistory.adapter = historyAdapter
        tracksConsumer = TrackListUpdateConsumer(
            this,
            trackListAdapter,
            recyclerTrackList,
            connectionError,
            nothingFoundError,
            progressBar
        )
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lastSearchQuery = editText.text.toString().trim()
                clearButton.visibility = clearButtonVisibility(s)
                if (lastSearchQuery.isEmpty()) {
                    showHistory()
                } else {
                    historyView.visibility = View.GONE
                }
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                inputText = s.toString()
            }
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun search(expression: String) {

        tracksSearchInteractor.search(expression, tracksConsumer)

        nothingFoundError.visibility = View.GONE
        connectionError.visibility = View.GONE
        recyclerTrackList.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun showHistory() {
        val history = searchHistoryInteractor.getTracksHistory()
        if (history.isNotEmpty()) {
            historyAdapter.updateTrackList(history)
            historyView.visibility = View.VISIBLE
            recyclerHistory.visibility = View.VISIBLE
            recyclerTrackList.visibility = View.GONE
            connectionError.visibility = View.GONE
            nothingFoundError.visibility = View.GONE
        } else {
            historyView.visibility = View.GONE
            recyclerHistory.visibility = View.GONE
        }
    }

    private fun update(lastSearchQuery: String) {
        search(lastSearchQuery)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (lastSearchQuery.isNotBlank()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }
}
