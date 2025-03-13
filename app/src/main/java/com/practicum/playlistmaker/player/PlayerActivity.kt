package com.practicum.playlistmaker.player

import android.annotation.SuppressLint
import android.media.MediaPlayer
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
import com.practicum.playlistmaker.search.SearchActivity.Companion.TRACK
import com.practicum.playlistmaker.search.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val STATE_UNAVAILABLE = 4

        private const val DELAY = 1000L
    }

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
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

        preparePlayer(track.previewUrl)
        playButton.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState != STATE_UNAVAILABLE) pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer(source: String?) {
        if (source != null) {
            mediaPlayer.setDataSource(source)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                playButton.isEnabled = true
                playerState = STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                stopPlayer()
            }
        } else playerState = STATE_UNAVAILABLE
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
            STATE_UNAVAILABLE -> Toast.makeText(
                applicationContext,
                R.string.track_unavailable,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setIconResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        startPlayingTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setIconResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
    }

    private fun stopPlayer() {
        mediaPlayer.pause()
        mediaPlayer.seekTo(0)
        playButton.setIconResource(R.drawable.ic_play)
        trackTimer.setText(R.string.track_start_time)
        playerState = STATE_PREPARED
    }

    private val handler = Handler(Looper.getMainLooper())
    private fun startPlayingTimer() {
        val playedMinutes = trackTimer.text.toString().substringBefore(":").toLong()
        val playedSeconds = trackTimer.text.toString().substringAfter(":").toLong()
        val playedTime =
            if (playedMinutes > 0) (60 * DELAY * playedMinutes) + (playedSeconds * DELAY) else playedSeconds * DELAY
        handler.post(createTimerTask(System.currentTimeMillis(), playedTime))
    }

    @SuppressLint("DefaultLocale")
    private fun createTimerTask(startTime: Long, playedTime: Long): Runnable {
        return object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    val nowPlayingTime = System.currentTimeMillis() - startTime
                    val displayedTime = (playedTime + nowPlayingTime) / DELAY
                    if (displayedTime <= 30) {
                        trackTimer.text =
                            String.format("%02d:%02d", displayedTime / 60, displayedTime % 60)
                        handler.postDelayed(this, DELAY)
                    } else {
                        stopPlayer()
                    }
                }
            }
        }
    }
}