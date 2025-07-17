package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.presentation.PlaylistsViewModel
import com.practicum.playlistmaker.library.presentation.models.PlaylistsScreenState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding get() = _binding!!

    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylists()

        viewModel.getPlaylistsStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is PlaylistsScreenState.Loading -> {
                    binding.progressBar.isVisible = true
                    switchContentScreen(listOf(), false)
                    switchEmptyScreen(false)
                }

                is PlaylistsScreenState.Content -> {
                    binding.progressBar.isVisible = false
                    switchContentScreen(screenState.playlists, true)
                    switchEmptyScreen(false)
                }

                is PlaylistsScreenState.Empty -> {
                    binding.progressBar.isVisible = false
                    switchContentScreen(listOf(), false)
                    switchEmptyScreen(true)
                }
            }

        }

        binding.buttonNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_playlistAddFragment)
        }
    }

    private fun switchContentScreen(content: List<Playlist>, shouldShow: Boolean) {
        if (shouldShow) {
            binding.run {
                playlists.layoutManager = GridLayoutManager(requireActivity(), 2)
                val playlistsAdapter = PlaylistsAdapter()
                playlistsAdapter.updatePlaylists(content)
                playlists.adapter = playlistsAdapter
                playlists.isVisible = true
            }
        } else binding.playlists.isVisible = false
    }

    private fun switchEmptyScreen(shouldShow: Boolean) {
        binding.run {
            noContentImage.isVisible = shouldShow
            noContentLabel.isVisible = shouldShow
        }
    }
}