package com.practicum.playlistmaker.library.ui

import android.content.Intent
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.LinearLayout
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
import com.practicum.playlistmaker.library.presentation.PlaylistsItemViewModel
import com.practicum.playlistmaker.library.presentation.models.PlaylistItemScreenState
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search.ui.SearchAdapter
import com.practicum.playlistmaker.util.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsItemFragment : Fragment() {

    companion object {
        private const val PLAYLIST = "PLAYLIST"
        fun createArgs(id: Int): Bundle = bundleOf(PLAYLIST to id)
    }

    private var _binding: FragmentPlaylistsItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistsItemViewModel>()

    private var playlistId: Int? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var deleteConfirmationDialog: MaterialAlertDialogBuilder

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

        binding.toolbarPlaylistsItem.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        playlistId = arguments?.getInt(PLAYLIST)
        if (playlistId != null) viewModel.getPlaylistById(playlistId!!)

        viewModel.getPlaylistWithTracksLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is PlaylistItemScreenState.Loading -> {
                    binding.progressBar.isVisible = true
                }

                is PlaylistItemScreenState.Content -> {
                    binding.run {
                        Glide.with(root)
                            .load(screenState.playlistInfo.coverPath)
                            .placeholder(R.drawable.ic_track_placeholder)
                            .into(binding.playlistCover)

                        playlistTitle.text = screenState.playlistInfo.title
                        playlistDescription.run {
                            text = screenState.playlistInfo.description
                            isVisible = !screenState.playlistInfo.description.isNullOrEmpty()
                        }
                        playlistDuration.text = resources.getQuantityString(
                            R.plurals.playlistDuration,
                            screenState.playlistInfo.duration,
                            screenState.playlistInfo.duration
                        )
                        playlistTracksCount.text = resources.getQuantityString(
                            R.plurals.numberOfTracks,
                            screenState.playlistInfo.tracks.size,
                            screenState.playlistInfo.tracks.size
                        )

                        bottomSheetBehavior =
                            BottomSheetBehavior.from(binding.playlistBottomSheet).apply {
                                var insets: Insets? = null
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    insets =
                                        requireActivity().windowManager.currentWindowMetrics.getWindowInsets()
                                            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
                                }
                                share.post {
                                    val targetElement = share.bottom
                                    val screenHeight = resources.displayMetrics.heightPixels
                                    val peekHeight = screenHeight - targetElement - (insets?.top
                                        ?: 0) - (insets?.bottom ?: 0) - (share.height * 3)
                                    bottomSheetBehavior.peekHeight = peekHeight
                                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                }
                            }
                        setBottomSheet(screenState.playlistInfo.tracks)

                        progressBar.isVisible = false
                    }
                }
            }
        }
    }

    private fun setBottomSheet(tracks: List<Track>) {
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.tracks.layoutManager = LinearLayoutManager(
                            requireActivity(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        val tracksAdapter = SearchAdapter({ track ->
                            startActivity(
                                Intent(
                                    requireActivity(), PlayerActivity::class.java
                                ).apply {
                                    putExtra("TRACK", Utils().serializeToJson(track))
                                })
                        }, { track ->
                            removeFromPlaylist(track)
                            true
                        })
                        tracksAdapter.updateTrackList(tracks)
                        binding.tracks.adapter = tracksAdapter
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

    private fun removeFromPlaylist(track: Track) {
        deleteConfirmationDialog = MaterialAlertDialogBuilder(requireActivity())
            .setMessage(R.string.track_remove_confirmation)
            .setNegativeButton(R.string.track_remove_reject) { _, _ ->
            }
            .setPositiveButton(R.string.track_remove_accept) { _, _ ->
                //TODO: viewModel.removeTrack(track, playlist)
            }
        deleteConfirmationDialog.show()
    }
}