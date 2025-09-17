package com.practicum.playlistmaker.player.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.presentation.models.PlayerScreenState
import com.practicum.playlistmaker.search.track_search.domain.models.Track
import com.practicum.playlistmaker.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerService : Service(), PlayerControl {

    companion object {
        const val SERIALIZED_TRACK = "SERIALIZED_TRACK"
        const val NOTIFICATION_CHANNEL_ID = "Service notification channel"
        const val SERVICE_NOTIFICATION_ID = 126345
    }

    private val _playerState = MutableStateFlow<PlayerScreenState>(PlayerScreenState.Loading)
    private val playerState: StateFlow<PlayerScreenState> = _playerState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var track: Track? = null
    private val binder = PlayerServiceBinder()
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        val serializedTrack = intent?.getStringExtra(SERIALIZED_TRACK) ?: ""
        track = Utils().createFromJson(serializedTrack, Track::class.java)

        initPlayer()

        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(300L)
                _playerState.value = PlayerScreenState.Playing(getCurrentPlayerPosition(), true)
            }
        }
    }

    private fun initPlayer() {
        if (track?.previewUrl.isNullOrEmpty()) {
            _playerState.value = PlayerScreenState.Error
            return
        }

        mediaPlayer?.setDataSource(track?.previewUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            track?.let { _playerState.value = PlayerScreenState.Prepared(it) }
        }
        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()
            track?.let { _playerState.value = PlayerScreenState.Prepared(it) }
            stopForeground()
        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _playerState.value = PlayerScreenState.Playing(getCurrentPlayerPosition(), true)
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerScreenState.Playing(getCurrentPlayerPosition(), false)
    }

    override fun getPlayerState(): StateFlow<PlayerScreenState> = playerState

    override fun startForeground() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun stopForeground() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun releasePlayer() {
        mediaPlayer?.stop()
        timerJob?.cancel()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun getCurrentPlayerPosition(): String {
        return Utils().formatTimeAsString(mediaPlayer?.currentPosition?.toLong() ?: 0L)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Player service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = getString(R.string.player_service_description)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("${track?.artistName ?: ""} - ${track?.trackName ?: ""}")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }
}