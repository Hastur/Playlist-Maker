package com.practicum.playlistmaker.library.ui

import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsItemBinding
import com.practicum.playlistmaker.library.domain.models.PlaylistInfo
import com.practicum.playlistmaker.library.presentation.PlaylistsItemViewModel
import com.practicum.playlistmaker.library.presentation.models.PlaylistItemScreenState
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import com.practicum.playlistmaker.search.track_search.ui.SearchAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsItemFragment : Fragment() {

    companion object {
        private const val PLAYLIST = "PLAYLIST"
        fun createArgs(id: Int): Bundle = bundleOf(PLAYLIST to id)
    }

    private var _binding: FragmentPlaylistsItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistsItemViewModel>()
    private val trackViewModel by viewModel<SearchViewModel>()

    private var playlistId: Int? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var contextMenuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = arguments?.getInt(PLAYLIST)
        if (playlistId != null) viewModel.getPlaylistById(playlistId!!)

        viewModel.getPlaylistWithTracksLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is PlaylistItemScreenState.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is PlaylistItemScreenState.Content -> {
                    setupScreen(screenState.playlistInfo)
                    setupBottomSheet(screenState.playlistInfo)
                    setupContextMenuBottomSheet(screenState.playlistInfo)
                    binding.progressBar.isVisible = false
                }
            }
        }

        trackViewModel.getSelectedTrackSingleEvent()
            .observe(viewLifecycleOwner) { serializedTrack ->
                findNavController().navigate(
                    R.id.action_playlistsItemFragment_to_playerActivity,
                    PlayerActivity.createArgs(serializedTrack)
                )
            }

        viewModel.getToastSingleEvent().observe(viewLifecycleOwner) { message ->
            Toast.makeText(
                requireActivity(),
                getString(message.first, message.second),
                Toast.LENGTH_LONG
            ).show()
            if (message.first == R.string.playlist_remove_complete) findNavController().popBackStack()
        }

        binding.run {
            toolbarPlaylistsItem.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            share.setOnClickListener {
                viewModel.sharePlaylist()
            }

            contextMenu.setOnClickListener {
                contextMenuBottomSheet.isVisible = true
                contextMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun setupScreen(playlistInfo: PlaylistInfo) {
        binding.run {
            Glide.with(root)
                .load(playlistInfo.coverPath)
                .placeholder(R.drawable.ic_track_placeholder)
                .into(playlistCover)

            playlistTitle.text = playlistInfo.title
            playlistDescription.run {
                text = playlistInfo.description
                isVisible = !playlistInfo.description.isNullOrEmpty()
            }
            playlistDuration.text = resources.getQuantityString(
                R.plurals.playlistDuration,
                playlistInfo.duration,
                playlistInfo.duration
            )
            playlistTracksCount.text = resources.getQuantityString(
                R.plurals.numberOfTracks,
                playlistInfo.tracks.size,
                playlistInfo.tracks.size
            )
        }
    }

    private fun setupBottomSheet(playlistInfo: PlaylistInfo) {
        bottomSheetBehavior =
            BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
                var insets: Insets? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    insets =
                        requireActivity().windowManager.currentWindowMetrics.windowInsets
                            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                }
                binding.share.post {
                    val targetElement = binding.share.bottom
                    val screenHeight = resources.displayMetrics.heightPixels
                    val peekHeight =
                        screenHeight - targetElement - (insets?.top ?: 0) - (insets?.bottom
                            ?: 0) - binding.share.height
                    bottomSheetBehavior.peekHeight = peekHeight
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

        binding.tracks.layoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
        val tracksAdapter = SearchAdapter({ track ->
            trackViewModel.openTrackWithDebounce(track)
        }, { track ->
            removeFromPlaylist(track.trackId, playlistInfo.id)
            true
        })
        tracksAdapter.updateTrackList(playlistInfo.tracks)
        binding.tracks.adapter = tracksAdapter

        viewModel.setPlural(
            resources.getQuantityString(
                R.plurals.numberOfTracks,
                playlistInfo.tracks.size,
                playlistInfo.tracks.size
            )
        )
    }

    private fun setupContextMenuBottomSheet(playlistInfo: PlaylistInfo) {
        contextMenuBottomSheetBehavior =
            BottomSheetBehavior.from(binding.contextMenuBottomSheet).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
        binding.run {
            Glide.with(root)
                .load(playlistInfo.coverPath)
                .placeholder(R.drawable.ic_track_placeholder)
                .into(playlist.playlistCover)
            playlist.playlistName.text = playlistInfo.title
            playlist.playlistSize.text = resources.getQuantityString(
                R.plurals.numberOfTracks,
                playlistInfo.tracks.size,
                playlistInfo.tracks.size
            )

            contextMenuShare.setOnClickListener {
                viewModel.sharePlaylist()
            }

            contextMenuEdit.setOnClickListener {
                //Todo next
            }

            contextMenuDelete.setOnClickListener {
                contextMenuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                removePlaylist(playlistInfo)
            }
        }

        setContextMenuBottomSheetCallback()
    }

    private fun setContextMenuBottomSheetCallback() {
        contextMenuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.run {
                    overlay.isVisible = true
                    overlay.alpha = (slideOffset + 1) / 2
                }
            }
        })
    }

    private fun removeFromPlaylist(trackId: Int, playlistId: Int) {
        MaterialAlertDialogBuilder(requireActivity())
            .setMessage(R.string.track_remove_confirmation)
            .setNegativeButton(R.string.remove_reject) { _, _ ->
            }
            .setPositiveButton(R.string.remove_accept) { _, _ ->
                viewModel.removeTrackFromPlaylist(trackId, playlistId)
            }
            .show()
    }

    private fun removePlaylist(playlistInfo: PlaylistInfo) {
        MaterialAlertDialogBuilder(requireActivity())
            .setMessage(
                resources.getString(R.string.playlist_remove_confirmation, playlistInfo.title)
            )
            .setNegativeButton(R.string.remove_reject) { _, _ ->
            }
            .setPositiveButton(R.string.remove_accept) { _, _ ->
                viewModel.removePlaylist(playlistInfo.id)
            }
            .show()
    }
}