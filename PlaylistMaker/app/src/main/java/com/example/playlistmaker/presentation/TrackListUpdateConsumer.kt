package com.example.playlistmaker.presentation

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.api.TracksSearchInteractor
import com.example.playlistmaker.domain.models.Track

class TrackListUpdateConsumer(
    private val activity: Activity,
    private val trackAdapter: TrackAdapter,
    private val recyclerTrackList:RecyclerView,
    private val connectionError: LinearLayout,
    private val nothingFoundError: LinearLayout,
    private val progressBar: ProgressBar
) : TracksSearchInteractor.TracksConsumer {
    companion object {
        private const val COMPLETE_CODE = 200
    }

    override fun consume(foundTracks: Pair<List<Track>, Int>) {
        val tracks = foundTracks.first
        val resultCode = foundTracks.second
        activity.runOnUiThread {
            progressBar.visibility = View.GONE
            if (tracks.isNotEmpty()) {
                trackAdapter.updateTrackList(tracks)
            } else {
                showError(resultCode)
            }
        }
    }

    private fun showError(resultCode: Int) {
        activity.runOnUiThread {
            when (resultCode) {
                COMPLETE_CODE -> {
                    trackAdapter.updateTrackList(emptyList())
                    nothingFoundError.visibility = View.VISIBLE
                }

                else -> {
                    recyclerTrackList.visibility = View.GONE
                    connectionError.visibility = View.VISIBLE
                }
            }
        }
    }
}
