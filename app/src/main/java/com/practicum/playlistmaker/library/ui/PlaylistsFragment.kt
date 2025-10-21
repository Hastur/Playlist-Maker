package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.compose_resources.PlaylistMakerTheme
import com.practicum.playlistmaker.library.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }

    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme { PlaylistsScreen() }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlaylists()

        viewModel.getButtonAddClickedSingle().observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {
                findNavController().navigate(R.id.action_libraryFragment_to_playlistAddFragment)
            }
        }

        viewModel.getPlaylistClickedSingle().observe(viewLifecycleOwner) { playlist ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_playlistsItemFragment,
                PlaylistsItemFragment.createArgs(playlist.id)
            )
        }
    }
}