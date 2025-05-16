package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityLibraryBinding

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.run {
            toolbarLibrary.setNavigationOnClickListener {
                this@LibraryActivity.finish()
            }

            libraryViewPager.adapter = LibraryViewPagerAdapter(supportFragmentManager, lifecycle)

            tabMediator = TabLayoutMediator(libraryTabs, libraryViewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.favorites)
                    1 -> tab.text = getString(R.string.playlists)
                }
            }
            tabMediator.attach()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}