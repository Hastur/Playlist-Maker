package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val TRACK = "TRACK"
        fun createArgs(serializedTrack: String): Bundle = bundleOf(TRACK to serializedTrack)
    }

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var serializedTrack: String
    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(serializedTrack)
    }

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

        serializedTrack = this.intent.getStringExtra(TRACK) ?: ""

        binding.run {
            toolbarPlayer.setNavigationOnClickListener {
                this@PlayerActivity.finish()
            }
            buttonPlay.setOnClickListener {
                viewModel.playOrPause()
            }
            buttonLike.setOnClickListener {
                viewModel.onFavoriteClick()
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

                is PlayerScreenState.Playing -> changeControlElementsStyle(screenState)
            }
        }

        viewModel.getFavoriteStateSingleEvent().observe(this) { inFavorites ->
            binding.buttonLike.run {
                if (inFavorites) {
                    setIconResource(R.drawable.ic_like_filled)
                    setIconTintResource(R.color.like_filled)
                } else {
                    setIconResource(R.drawable.ic_like)
                    setIconTintResource(R.color.white)
                }
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
            valueTrackTime.text = trackModel.trackTime
            if (trackModel.collectionName == null) {
                labelAlbum.isVisible = false
                valueAlbum.isVisible = false
            } else valueAlbum.text = trackModel.collectionName
            if (trackModel.releaseDate == null) {
                labelYear.isVisible = false
                valueYear.isVisible = false
            } else valueYear.text = trackModel.releaseDate
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
            playingTime.text = playingStatus.playingTime
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
    }
}