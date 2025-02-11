package com.practicum.playlistmaker.player

import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.SearchActivity.Companion.TRACK
import com.practicum.playlistmaker.search.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Toolbar>(R.id.toolbar_player).setNavigationOnClickListener {
            this.finish()
        }

        val track = (applicationContext as App).createFromJson(
            this.intent.getStringExtra(TRACK) ?: "", Track::class.java
        )

        val cornerRadiusToPx =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics)
                .toInt()
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .transform(RoundedCorners(cornerRadiusToPx))
            .into(findViewById<ImageView>(R.id.track_cover))

        findViewById<TextView>(R.id.track_name).text = track.trackName
        findViewById<TextView>(R.id.artist_name).text = track.artistName
        findViewById<TextView>(R.id.playing_time).text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        findViewById<TextView>(R.id.value_track_time).text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        findViewById<TextView>(R.id.value_album).text = track.collectionName
        findViewById<TextView>(R.id.value_year).text =
            SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate)
        findViewById<TextView>(R.id.value_genre).text = track.primaryGenreName
        findViewById<TextView>(R.id.value_country).text = track.country
    }
}