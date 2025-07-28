package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.library.presentation.FavoritesViewModel
import com.practicum.playlistmaker.library.presentation.models.FavoritesScreenState
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import com.practicum.playlistmaker.search.track_search.ui.SearchAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding get() = _binding!!

    private val viewModel by viewModel<FavoritesViewModel>()
    private val trackViewModel by viewModel<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getFavoritesStateLiveData().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is FavoritesScreenState.Loading -> {
                    binding.progressBar.isVisible = true
                    switchContentScreen(listOf(), false)
                    switchEmptyScreen(false)
                }

                is FavoritesScreenState.Content -> {
                    binding.progressBar.isVisible = false
                    switchContentScreen(screenState.favoriteTracks, true)
                    switchEmptyScreen(false)
                }

                is FavoritesScreenState.Empty -> {
                    binding.progressBar.isVisible = false
                    switchContentScreen(listOf(), false)
                    switchEmptyScreen(true)
                }
            }
        }

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

    private fun switchContentScreen(content: List<Track>, shouldShow: Boolean) {
        if (shouldShow) {
            binding.run {
                favoritesList.layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
                val favoritesAdapter = SearchAdapter(
                    { track ->
                        trackViewModel.openTrackWithDebounce(track.copy(isFavorite = true))
                    },
                    { false })
                favoritesAdapter.updateTrackList(content)
                favoritesList.adapter = favoritesAdapter
                favoritesList.isVisible = true
            }
        } else binding.favoritesList.isVisible = false
    }

    private fun switchEmptyScreen(shouldShow: Boolean) {
        binding.run {
            noContentImage.isVisible = shouldShow
            noContentLabel.isVisible = shouldShow
        }
    }
}