package com.practicum.playlistmaker.search.track_search.ui

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.compose_resources.PlaylistMakerTheme
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.track_search.presentation.SearchViewModel
import com.practicum.playlistmaker.util.NetworkBroadcastReceiver
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel by viewModel<SearchViewModel>()

    private val networkBroadcastReceiver = NetworkBroadcastReceiver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PlaylistMakerTheme { SearchScreen() }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSelectedTrackSingleEvent().observe(viewLifecycleOwner) { serializedTrack ->
            findNavController().navigate(
                R.id.action_searchFragment_to_playerActivity,
                PlayerActivity.createArgs(serializedTrack)
            )
        }
    }

    override fun onResume() {
        super.onResume()

        @Suppress("DEPRECATION")
        ContextCompat.registerReceiver(
            requireActivity(),
            networkBroadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onPause() {
        super.onPause()

        requireActivity().unregisterReceiver(networkBroadcastReceiver)
    }
}