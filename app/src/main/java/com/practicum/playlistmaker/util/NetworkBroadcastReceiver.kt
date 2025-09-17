package com.practicum.playlistmaker.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import com.practicum.playlistmaker.R

@Suppress("DEPRECATION")
class NetworkBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION && context != null) {
            if (!Utils().isNetworkAvailable(context)) Toast.makeText(
                context,
                R.string.internet_connection_lost,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}