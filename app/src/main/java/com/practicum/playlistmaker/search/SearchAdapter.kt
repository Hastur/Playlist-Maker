package com.practicum.playlistmaker.search

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R

class SearchAdapter(private val trackList: TrackList) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
    )

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(trackList.tracks[position])
    }

    override fun getItemCount() = trackList.tracks.size

    class SearchViewHolder(private val searchItem: View) : RecyclerView.ViewHolder(searchItem) {

        fun bind(model: Track) {
            val cornerRadiusToPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                2F,
                itemView.resources.displayMetrics
            ).toInt()
            Glide.with(searchItem)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.ic_library)
                .fitCenter()
                .transform(RoundedCorners(cornerRadiusToPx))
                .into(searchItem.findViewById<ImageView>(R.id.track_cover))

            searchItem.findViewById<TextView>(R.id.song_name).text = model.trackName
            searchItem.findViewById<TextView>(R.id.artist_name).text = model.artistName
            searchItem.findViewById<TextView>(R.id.track_time).text = model.trackTime
        }
    }
}