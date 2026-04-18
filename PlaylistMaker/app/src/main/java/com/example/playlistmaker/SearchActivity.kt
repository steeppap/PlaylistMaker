package com.example.playlistmaker

import android.content.SharedPreferences
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.toString

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val I_TUNES_BASE_URL = "https://itunes.apple.com"
        private const val EDIT_TEXT_KEY = "editTextKey"
        private const val COMPLETE_CODE = 200
        private const val FAIL_CODE = 0
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private lateinit var retrofit: Retrofit
    private lateinit var iTunesService: ITunesApi
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
    private lateinit var prefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var progressBar: ProgressBar
    private lateinit var handler: Handler
    private lateinit var searchRunnable: Runnable


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
            searchHistory.clear()
            historyAdapter.updateTrackList(searchHistory.getHistory())
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
        retrofit = Retrofit.Builder()
            .baseUrl(I_TUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        iTunesService = retrofit.create(ITunesApi::class.java)
        prefs = getSharedPreferences(App.PREFS_NAME, MODE_PRIVATE)
        searchHistory = SearchHistory(prefs)
        handler = Handler(Looper.getMainLooper())
        searchRunnable = Runnable { search(lastSearchQuery) }

        trackListAdapter = TrackAdapter(trackList) { track ->
            searchHistory.addTrack(track)
        }

        historyAdapter = TrackAdapter(searchHistory.getHistory().toMutableList()) { track ->
            searchHistory.addTrack(track)
            showHistory()
        }
        recyclerTrackList.adapter = trackListAdapter
        recyclerHistory.adapter = historyAdapter

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

    private fun search(query: String) {
        lastSearchQuery = query
        nothingFoundError.visibility = View.GONE
        connectionError.visibility = View.GONE
        recyclerTrackList.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        if (lastSearchQuery.isNotEmpty()) {
            iTunesService.search(lastSearchQuery).enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    if (response.code() == COMPLETE_CODE) {
                        trackList.clear()
                        progressBar.visibility = View.GONE
                        if (response.body()?.results?.isNotEmpty() == true) {
                            trackList.addAll(response.body()?.results!!)
                            trackListAdapter.notifyDataSetChanged()
                        }
                        if (trackList.isEmpty()) {
                            showError(response.code())
                        }
                    } else {
                        showError(response.code())
                    }
                }

                override fun onFailure(
                    call: Call<ITunesResponse?>?,
                    t: Throwable?
                ) {
                    showError(FAIL_CODE)
                }
            })
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun showError(codeError: Int) {
        if (codeError == COMPLETE_CODE) {
            recyclerTrackList.visibility = View.GONE
            nothingFoundError.visibility = View.VISIBLE
            connectionError.visibility = View.GONE
            progressBar.visibility = View.GONE

        } else {
            recyclerTrackList.visibility = View.GONE
            connectionError.visibility = View.VISIBLE
            nothingFoundError.visibility = View.GONE
            progressBar.visibility = View.GONE

        }
    }

    private fun showHistory() {
        val history = searchHistory.getHistory()
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
        connectionError.visibility = View.GONE
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        if (lastSearchQuery.isNotBlank()) {
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
        }
    }
}
