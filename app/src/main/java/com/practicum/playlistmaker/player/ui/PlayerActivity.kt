package com.practicum.playlistmaker.player.ui

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.ui.PlaylistAddFragment
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.player.services.PlayerService
import com.practicum.playlistmaker.player.services.PlayerService.Companion.SERIALIZED_TRACK
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.util.NetworkBroadcastReceiver
import com.practicum.playlistmaker.util.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val TRACK = "TRACK"
        fun createArgs(serializedTrack: String): Bundle = bundleOf(TRACK to serializedTrack)
    }

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var serializedTrack: String
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val viewModel by viewModel<PlayerViewModel> {
        parametersOf(serializedTrack)
    }

    private val networkBroadcastReceiver = NetworkBroadcastReceiver()
    private var playerServiceIntent: Intent? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.PlayerServiceBinder
            viewModel.setPlayerControl(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removeAudioPlayerControl()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.showServiceNotification()
            } else {
                Toast.makeText(this, R.string.no_player_permission, Toast.LENGTH_LONG).show()
            }
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

        playerServiceIntent = Intent(this, PlayerService::class.java).apply {
            putExtra(SERIALIZED_TRACK, serializedTrack)
        }

        bindPlayerService()

        binding.run {
            toolbarPlayer.setNavigationOnClickListener {
                this@PlayerActivity.finish()
            }
            buttonPlay.setOnClickListener {
                viewModel.playOrPause()
            }
            buttonLike.run {
                setOnClickListener { viewModel.onFavoriteClick() }
                val track = Utils().createFromJson(serializedTrack, Track::class.java)
                if (track.isFavorite) {
                    setIconResource(R.drawable.ic_like_filled)
                    setIconTintResource(R.color.like_filled)
                }
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
                    binding.run {
                        playingTime.text = getString(R.string.track_start_time)
                        buttonPlay.setPlayingState(false)
                    }
                }

                is PlayerScreenState.Playing -> {
                    binding.run {
                        playingTime.text = screenState.playingTime
                        if (!screenState.isPlaying) buttonPlay.setPlayingState(false)
                    }
                }
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

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.buttonAddToPlaylist.setOnClickListener {
            viewModel.getPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewModel.getPlaylistsSingleEvent().observe(this) { playlists ->
            setBottomSheet(playlists)
        }

        binding.buttonNewPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            supportFragmentManager.commit {
                add(R.id.player_container_view, PlaylistAddFragment.newInstance(true))
                addToBackStack(null)
            }
        }

        viewModel.getAddingResultSingleEvent().observe(this) { isAddedToPlaylist ->
            if (isAddedToPlaylist) {
                Toast.makeText(
                    this,
                    getString(R.string.add_to_playlist_success),
                    Toast.LENGTH_SHORT
                ).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else Toast.makeText(
                this,
                getString(R.string.add_to_playlist_exists),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.hideServiceNotification()

        @Suppress("DEPRECATION")
        ContextCompat.registerReceiver(
            this,
            networkBroadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
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

    override fun onPause() {
        super.onPause()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.showServiceNotification()
        }

        unregisterReceiver(networkBroadcastReceiver)
    }

    override fun onDestroy() {
        unbindPlayerService()
        super.onDestroy()
    }

    private fun setBottomSheet(playlists: List<Playlist>) {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.playlists.layoutManager = LinearLayoutManager(
                            this@PlayerActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        val playlistsAdapter = AddToPlaylistAdapter { playlist ->
                            viewModel.addToPlaylist(playlist)
                        }
                        playlistsAdapter.updatePlaylists(playlists)
                        binding.playlists.adapter = playlistsAdapter
                    }

                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.run {
                    overlay.isVisible = true
                    overlay.alpha = (slideOffset + 1) / 2
                }
            }
        })
    }

    private fun bindPlayerService() {
        playerServiceIntent?.let { bindService(it, serviceConnection, BIND_AUTO_CREATE) }
    }

    private fun unbindPlayerService() {
        unbindService(serviceConnection)
    }
}