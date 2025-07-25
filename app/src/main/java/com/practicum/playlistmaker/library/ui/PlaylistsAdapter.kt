package com.practicum.playlistmaker.library.ui

import android.annotation.SuppressLint
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.library.domain.models.Playlist

class PlaylistsAdapter : RecyclerView.Adapter<PlaylistsAdapter.PlaylistsViewHolder>() {

    private var playlists = listOf<Playlist>()

    @SuppressLint("NotifyDataSetChanged")
    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PlaylistsViewHolder(
        ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size

    inner class PlaylistsViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Playlist) {
            val cornerRadiusToDp =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    8F,
                    itemView.resources.displayMetrics
                ).toInt()
            binding.run {
                Glide.with(root)
                    .load(model.coverPath.toUri())
                    .placeholder(R.drawable.ic_track_placeholder)
                    .transform(RoundedCorners(cornerRadiusToDp))
                    .into(binding.playlistCover)

                playlistName.text = model.name
                playlistSize.text = itemView.resources.getQuantityString(
                    R.plurals.numberOfTracks,
                    model.tracksIds.size,
                    model.tracksIds.size
                )
            }
        }
    }
}