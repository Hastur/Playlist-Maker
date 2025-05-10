package com.practicum.playlistmaker.search.track_search.ui

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class SearchAdapter(private val clickListener: (Track) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    var trackList = listOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
    )

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount() = trackList.size

    inner class SearchViewHolder(private val searchItem: View) :
        RecyclerView.ViewHolder(searchItem) {

        fun bind(model: Track) {
            val cornerRadiusToPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                2F,
                itemView.resources.displayMetrics
            ).toInt()
            Glide.with(searchItem)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.ic_track_placeholder)
                .fitCenter()
                .transform(RoundedCorners(cornerRadiusToPx))
                .into(searchItem.findViewById(R.id.track_cover))

            searchItem.findViewById<TextView>(R.id.song_name).text = model.trackName
            val artistName = searchItem.findViewById<TextView>(R.id.artist_name)
            artistName.text = model.artistName
            artistName.requestLayout()
            searchItem.findViewById<TextView>(R.id.track_time).text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(model.trackTimeMillis)

            searchItem.setOnClickListener {
                clickListener(model)
            }
        }
    }
}