package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.button_search)
        val libraryButton = findViewById<Button>(R.id.button_library)
        val settingsButton = findViewById<Button>(R.id.button_settings)

        val buttonClickListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Toast.makeText(this@MainActivity, "Переходим к поиску", Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(buttonClickListener)

        libraryButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Переходим в библиотеку", Toast.LENGTH_SHORT).show()
        }

        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Переходим к настройкам", Toast.LENGTH_SHORT).show()
        }
    }
}