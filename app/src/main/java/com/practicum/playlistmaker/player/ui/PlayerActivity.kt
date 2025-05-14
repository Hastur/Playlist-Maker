package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.util.Utils
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.search.track_search.ui.SearchActivity.Companion.TRACK
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track
    private val viewModel by viewModels<PlayerViewModel> { PlayerViewModel.getViewModelFactory(track) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        track = Utils().createFromJson(this.intent.getStringExtra(TRACK) ?: "", Track::class.java)

        binding.run {
            toolbarPlayer.setNavigationOnClickListener {
                this@PlayerActivity.finish()
            }
            buttonPlay.setOnClickListener {
                viewModel.playOrPause()
            }
        }

        viewModel.getScreenStateLiveData().observe(this) { screenState ->
            when (screenState) {
                is PlayerScreenState.Loading -> changeContentVisibility(loading = true)

                is PlayerScreenState.Error -> Toast.makeText(
                    applicationContext,
                    R.string.track_unavailable,
                    Toast.LENGTH_SHORT
                ).show()

                is PlayerScreenState.Prepared -> {
                    changeContentVisibility(loading = false)
                    setContent(screenState.trackModel)
                }

                is PlayerScreenState.Playing -> {changeControlElementsStyle(screenState)
                Log.e("IsPlaying", screenState.isPlaying.toString())}
            }
        }
    }

    private fun setContent(trackModel: Track) {
        val cornerRadiusToDp =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics)
                .toInt()
        Glide.with(this)
            .load(trackModel.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .transform(RoundedCorners(cornerRadiusToDp))
            .into(binding.trackCover)

        binding.run {
            trackName.text = trackModel.trackName
            artistName.text = trackModel.artistName
            valueTrackTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackModel.trackTimeMillis)
            if (trackModel.collectionName == null) {
                labelAlbum.isVisible = false
                valueAlbum.isVisible = false
            } else valueAlbum.text = trackModel.collectionName
            if (trackModel.releaseDate == null) {
                labelYear.isVisible = false
                valueYear.isVisible = false
            } else valueYear.text =
                SimpleDateFormat("yyyy", Locale.getDefault()).format(trackModel.releaseDate)
            valueGenre.text = trackModel.primaryGenreName
            valueCountry.text = trackModel.country
        }
    }

    private fun changeContentVisibility(loading: Boolean) {
        binding.run {
            progressBar.isVisible = loading
            allVisibleElements.isVisible = !loading
        }
    }

    private fun changeControlElementsStyle(playingStatus: PlayerScreenState.Playing) {
        binding.run {
            buttonPlay.setIconResource(
                if (playingStatus.isPlaying) R.drawable.ic_pause
                else R.drawable.ic_play
            )
            playingTime.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(playingStatus.playingTime)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }
}