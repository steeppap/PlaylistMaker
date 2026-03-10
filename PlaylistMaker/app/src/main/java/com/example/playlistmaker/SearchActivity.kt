package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
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
        private const val COMPLITE_CODE = 200
        private const val FAIL_CODE = 0
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(I_TUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private lateinit var editText: EditText
    private lateinit var backButton: Button
    private lateinit var updateButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var clearButton: ImageView
    private lateinit var connectionError: LinearLayout
    private lateinit var nothingFoundError: LinearLayout
    private var inputText: String = ""
    private var trackList = mutableListOf<Track>()
    private lateinit var recyclerTrackList: RecyclerView
    private lateinit var recyclerHistory: RecyclerView
    private lateinit var historyView: View
    private lateinit var trackListAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private var lastSearchQuery: String = ""
    private lateinit var searchHistory: SearchHistory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        searchHistory = SearchHistory(prefs)

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

        trackListAdapter = TrackAdapter(trackList) { track ->
            searchHistory.addTrack(track)
        }

        historyAdapter = TrackAdapter(searchHistory.getHistory()) { track ->
            searchHistory.addTrack(track)
            showHistory()
        }

        recyclerTrackList.adapter = trackListAdapter
        recyclerHistory.adapter = historyAdapter

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }

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

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
                historyView.visibility = View.GONE
            }

            override fun afterTextChanged(s: Editable?) {

                inputText = s.toString()
            }
        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHistory()
            }
        }

        editText.addTextChangedListener(textWatcher)

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) {
                    search(text)
                    hideKeyboard()
                }
                true
            } else false
        }

        updateButton.setOnClickListener {
            update(lastSearchQuery)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("editTextKey", inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedText = savedInstanceState.getString("editTextKey")
        editText.setText(savedText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
    private fun search(text: String) {
        lastSearchQuery = text
        nothingFoundError.visibility = View.GONE
        connectionError.visibility = View.GONE
        recyclerTrackList.visibility = View.VISIBLE

        if (lastSearchQuery.isNotEmpty()) {
            iTunesService.search(lastSearchQuery).enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>,
                    response: Response<ITunesResponse>
                ) {
                    if (response.code() == COMPLITE_CODE) {
                        trackList.clear()
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
        if (codeError == 200) {
            recyclerTrackList.visibility = View.GONE
            nothingFoundError.visibility = View.VISIBLE
            connectionError.visibility = View.GONE
        } else {
            recyclerTrackList.visibility = View.GONE
            connectionError.visibility = View.VISIBLE
            nothingFoundError.visibility = View.GONE

        }
    }

    private fun showHistory() {
        val history = searchHistory.getHistory()
        if (history.isNotEmpty()) {
            historyView.visibility = View.VISIBLE
            recyclerHistory.visibility = View.VISIBLE
            recyclerTrackList.visibility = View.GONE
            connectionError.visibility = View.GONE
            nothingFoundError.visibility = View.GONE
            historyAdapter.updateTrackList(history)
        }
        else{
            historyView.visibility = View.GONE
            recyclerHistory.visibility = View.GONE
        }
    }

    private fun update(lastSearchQuery: String) {
        search(lastSearchQuery)
        connectionError.visibility = View.GONE
    }
}
