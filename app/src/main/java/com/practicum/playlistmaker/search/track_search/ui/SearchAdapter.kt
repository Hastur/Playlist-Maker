package com.practicum.playlistmaker.search.track_search.ui

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemTrackBinding
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class SearchAdapter(private val clickListener: (Track) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    var trackList = listOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchViewHolder(
        ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount() = trackList.size

    inner class SearchViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Track) {
            val cornerRadiusToDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                2F,
                itemView.resources.displayMetrics
            ).toInt()
            binding.run {
                Glide.with(root)
                    .load(model.artworkUrl100)
                    .placeholder(R.drawable.ic_track_placeholder)
                    .fitCenter()
                    .transform(RoundedCorners(cornerRadiusToDp))
                    .into(binding.trackCover)

                songName.text = model.trackName
                artistName.text = model.artistName
                artistName.requestLayout()
                trackTime.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(model.trackTimeMillis)

                root.setOnClickListener { clickListener(model) }
            }
        }
    }
}