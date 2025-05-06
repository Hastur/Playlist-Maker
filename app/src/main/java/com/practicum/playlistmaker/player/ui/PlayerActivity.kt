package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.Utils
import com.practicum.playlistmaker.search.Creator
import com.practicum.playlistmaker.search.ui.SearchActivity.Companion.TRACK
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PlayerActivity : AppCompatActivity() {
    private val player = Creator.providePlayerInteractor()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var playButton: MaterialButton
    private lateinit var trackTimer: TextView

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

        val track =
            Utils().createFromJson(this.intent.getStringExtra(TRACK) ?: "", Track::class.java)
        playButton = findViewById(R.id.button_play)
        trackTimer = findViewById(R.id.playing_time)

        val cornerRadiusToPx =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8F, resources.displayMetrics)
                .toInt()
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .transform(RoundedCorners(cornerRadiusToPx))
            .into(findViewById(R.id.track_cover))

        findViewById<TextView>(R.id.track_name).text = track.trackName
        findViewById<TextView>(R.id.artist_name).text = track.artistName
        findViewById<TextView>(R.id.value_track_time).text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        val album = findViewById<TextView>(R.id.value_album)
        if (track.collectionName == null) {
            findViewById<TextView>(R.id.label_album).isVisible = false
            album.isVisible = false
        } else album.text = track.collectionName
        val releaseDate = findViewById<TextView>(R.id.value_year)
        if (track.releaseDate == null) {
            findViewById<TextView>(R.id.label_year).isVisible = false
            releaseDate.isVisible = false
        } else releaseDate.text =
            SimpleDateFormat("yyyy", Locale.getDefault()).format(track.releaseDate)
        findViewById<TextView>(R.id.value_genre).text = track.primaryGenreName
        findViewById<TextView>(R.id.value_country).text = track.country

        if (track.previewUrl != null) {
            player.preparePlayer(
                track.previewUrl,
                {
                    playButton.isEnabled = true
                },
                {
                    playButton.setIconResource(R.drawable.ic_play)
                    trackTimer.setText(R.string.track_start_time)
                }
            )
        } else Toast.makeText(applicationContext, R.string.track_unavailable, Toast.LENGTH_SHORT)
            .show()

        playButton.setOnClickListener {
            player.controlPlayer(
                {
                    playButton.setIconResource(R.drawable.ic_pause)
                    handler.post(timerTask)
                },
                {
                    playButton.setIconResource(R.drawable.ic_play)
                }
            )
        }
    }

    override fun onPause() {
        super.onPause()
        player.pausePlayer { playButton.setIconResource(R.drawable.ic_play) }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.releasePlayer()
        handler.removeCallbacks(timerTask)
    }

    @SuppressLint("DefaultLocale")
    private val timerTask = object : Runnable {
        override fun run() {
            val duration =
                player.getPlayingTime().toLong().toDuration(DurationUnit.MILLISECONDS)
            val time = duration.toComponents { minutes, seconds, _ ->
                String.format("%02d:%02d", minutes, seconds)
            }
            trackTimer.text = time
            handler.postDelayed(this, 1000L)
        }
    }
}