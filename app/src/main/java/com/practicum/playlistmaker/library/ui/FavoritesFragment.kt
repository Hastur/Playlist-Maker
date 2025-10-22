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
import com.practicum.playlistmaker.library.presentation.FavoritesViewModel
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }

    private val viewModel by viewModel<FavoritesViewModel>()
    private val trackViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme { FavoritesScreen() }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trackViewModel.getSelectedTrackSingleEvent()
            .observe(viewLifecycleOwner) { serializedTrack ->
                findNavController().navigate(
                    R.id.action_libraryFragment_to_playerActivity,
                    PlayerActivity.createArgs(serializedTrack)
                )
            }
    }

    override fun onResume() {
        super.onResume()

        viewModel.getFavorites()

    }
}